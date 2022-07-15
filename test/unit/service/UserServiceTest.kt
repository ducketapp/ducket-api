package io.ducket.api.unit.service

import io.ducket.api.BaseUnitTest
import io.ducket.api.domain.mapper.UserMapper
import io.ducket.api.domain.model.user.UserCreate
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.user.dto.UserDto
import io.ducket.api.domain.repository.*
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.UserService
import io.ducket.api.plugins.AuthenticationException
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException
import io.ducket.api.test_data.AccountObjectMother
import io.ducket.api.test_data.UserObjectMother
import io.ducket.api.utils.HashUtils
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import org.junit.jupiter.api.Test

internal class UserServiceTest : BaseUnitTest() {
    private val userRepositoryMock = mockk<UserRepository>()
    private val accountServiceMock = mockk<AccountService>()
    private val cut = UserService(userRepositoryMock, accountServiceMock)

    @Test
    fun should_ReturnUser_When_GetExistingUser() {
        // given
        val user = UserObjectMother.user()
        val expected = UserMapper.mapModelToDto(user)
        coEvery { userRepositoryMock.findOne(user.id) } returns user

        // when
        val actual = runBlocking { cut.getUser(user.id) }

        // then
        actual shouldBe expected
    }

    @Test
    fun should_ThrowException_When_GetNonExistentUser() {
        // given
        coEvery { userRepositoryMock.findOne(userId) } returns null

        // when
        val executable = { runBlocking { cut.getUser(userId) } }

        // then
        shouldThrowExactly<NoDataFoundException>(executable).also {
            it.message shouldBe NoDataFoundException().message
        }
    }

    @Test
    fun should_ThrowException_When_AuthenticateWithNonExistentUser() {
        // given
        val authUserDto = UserObjectMother.userAuthDto()
        coEvery { userRepositoryMock.findOneByEmail(authUserDto.email) } returns null

        // when
        val executable = { runBlocking { cut.authenticateUser(authUserDto) } }

        // then
        shouldThrowExactly<AuthenticationException>(executable).also {
            it.message shouldBe AuthenticationException().message
        }
    }

    @Test
    fun should_ReturnUser_When_AuthenticateWithExistingUser() {
        // given
        val authUserDtoPasswordSlot = slot<String>()
        val userPasswordHashSlot = slot<String>()
        val authUserDto = UserObjectMother.userAuthDto()
        val user = UserObjectMother.user()
        val expected = UserDto(user)
        mockkObject(HashUtils)

        coEvery { userRepositoryMock.findOneByEmail(authUserDto.email) } returns user
        coEvery { HashUtils.check(capture(authUserDtoPasswordSlot), capture(userPasswordHashSlot)) } returns true

        // when
        val actual = runBlocking { cut.authenticateUser(authUserDto) }

        // then
        actual shouldBe expected
        authUserDtoPasswordSlot.captured shouldBe authUserDto.password
        userPasswordHashSlot.captured shouldBe user.passwordHash
    }

    @Test
    fun should_ThrowException_When_AuthenticateExistingUserWithInvalidPassword() {
        // given
        val authUserDtoPasswordSlot = slot<String>()
        val userPasswordHashSlot = slot<String>()
        val authUserDto = UserObjectMother.userAuthDto()
        val user = UserObjectMother.user()
        mockkObject(HashUtils)

        coEvery { userRepositoryMock.findOneByEmail(authUserDto.email) } returns user
        coEvery { HashUtils.check(capture(authUserDtoPasswordSlot), capture(userPasswordHashSlot)) } returns false

        // when
        val executable = { runBlocking { cut.authenticateUser(authUserDto) } }

        // then
        shouldThrowExactly<AuthenticationException>(executable).also {
            it.message shouldBe "Either password or email is incorrect"
        }
        authUserDtoPasswordSlot.captured shouldBe authUserDto.password
        userPasswordHashSlot.captured shouldBe user.passwordHash
    }

    @Test
    fun should_ReturnNewUser_When_CreateUser() {
        // given
        val cutSpy = spyk(cut)
        val transactionSlot = slot<suspend Transaction.() -> Any>()
        val accountCreateDtoSlot = slot<AccountCreateDto>()
        val userCreateSlot = slot<UserCreate>()
        val userIdSlot = slot<Long>()

        val userCreateDto = UserObjectMother.userCreateDto()
        val accountCreateDto = AccountObjectMother.accountCreateDto()
        val accountDto = AccountObjectMother.accountDto()

        val user = UserObjectMother.user()
        val userCreate = UserObjectMother.userCreate()
        val expected = UserObjectMother.userDto()

        mockkObject(HashUtils)
        mockkStatic(ThreadLocalTransactionManager::class)

        coEvery { HashUtils.hash(any()) } returns userCreate.passwordHash
        coEvery { userRepositoryMock.findOneByEmail(userCreate.email) } returns null
        coEvery { userRepositoryMock.createOne(capture(userCreateSlot)) } returns user
        coEvery { accountServiceMock.createAccount(capture(userIdSlot), capture(accountCreateDtoSlot)) } returns accountDto
        coEvery { cutSpy.blockingTransaction(capture(transactionSlot)) } coAnswers { transactionSlot.coInvoke(mockk()) }

        // when
        val actual = runBlocking { cutSpy.createUser(userCreateDto) }

        // then
        actual shouldBe expected
        userCreateSlot.captured shouldBe userCreate
        accountCreateDtoSlot.captured shouldBe accountCreateDto
        userIdSlot.captured shouldBe user.id
    }

    @Test
    fun should_ThrowException_When_CreateExistingUser() {
        // given
        val userCreateDto = UserObjectMother.userCreateDto()
        val user = UserObjectMother.user()

        coEvery { userRepositoryMock.findOneByEmail(userCreateDto.email) } returns user

        // when
        val executable = { runBlocking { cut.createUser(userCreateDto) }}

        // then
        shouldThrowExactly<DuplicateDataException>(executable).also {
            it.message shouldBe "Such email has already been taken"
        }
        coVerify(exactly = 1) { userRepositoryMock.createOne(any()) wasNot Called }
        coVerify(exactly = 1) { accountServiceMock.createAccount(any(), any()) wasNot Called }
    }

    @Test
    fun should_ReturnUpdatedUser_When_UpdateExistingUser() {
        // given
        val userIdSlot = slot<Long>()
        val userUpdate = UserObjectMother.userUpdate()
        val userUpdateDto = UserObjectMother.userUpdateDto()
        val user = UserObjectMother.user()
        val expected = UserObjectMother.userDto()
        mockkObject(HashUtils)

        coEvery { HashUtils.hash(any()) } returns userUpdate.passwordHash
        coEvery { userRepositoryMock.updateOne(capture(userIdSlot), userUpdate) } returns user

        // when
        val actual = runBlocking { cut.updateUser(user.id, userUpdateDto) }

        // then
        actual shouldBe expected
        userIdSlot.captured shouldBe user.id
    }

    @Test
    fun should_ReturnException_When_UpdateNonExistentUser() {
        // given
        val userUpdate = UserObjectMother.userUpdate()
        val userUpdateDto = UserObjectMother.userUpdateDto()
        mockkObject(HashUtils)

        coEvery { HashUtils.hash(any()) } returns userUpdate.passwordHash
        coEvery { userRepositoryMock.updateOne(userId, userUpdate) } returns null

        // when
        val executable = { runBlocking { cut.updateUser(userId, userUpdateDto) } }

        // then
        shouldThrowExactly<NoDataFoundException>(executable).also {
            it.message shouldBe NoDataFoundException().message
        }
    }

    @Test
    fun should_DeleteUser_When_DeleteUser() {
        // given
        val cutSpy = spyk(cut)
        val transactionSlot = slot<suspend Transaction.() -> Any>()
        mockkStatic(ThreadLocalTransactionManager::class)

        coEvery { userRepositoryMock.deleteData(userId) } returns Unit
        coEvery { userRepositoryMock.deleteOne(userId) } returns Unit
        coEvery { cutSpy.blockingTransaction(capture(transactionSlot)) } coAnswers { transactionSlot.coInvoke(mockk()) }

        // when
        val actual = runBlocking { cutSpy.deleteUser(userId) }

        // then
        actual shouldBe Unit
        coVerify(exactly = 1) { userRepositoryMock.deleteData(userId) }
        coVerify(exactly = 1) { userRepositoryMock.deleteOne(userId) }
    }

    @Test
    fun should_DeleteUserData_When_DeleteUserData() {
        // given
        val cutSpy = spyk(cut)
        val transactionSlot = slot<suspend Transaction.() -> Any>()
        mockkStatic(ThreadLocalTransactionManager::class)

        coEvery { userRepositoryMock.deleteData(userId) } returns Unit
        coEvery { cutSpy.blockingTransaction(capture(transactionSlot)) } coAnswers { transactionSlot.coInvoke(mockk()) }

        // when
        val actual = runBlocking { cutSpy.deleteUserData(userId) }

        // then
        actual shouldBe Unit
        coVerify(exactly = 1) { userRepositoryMock.deleteData(userId) }
    }
}
