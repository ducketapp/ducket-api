package io.ducket.api

import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.repository.*
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.UserService
import io.ducket.api.plugins.AuthenticationException
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException
import io.ducket.api.test_data.AccountObjectMother
import io.ducket.api.test_data.UserObjectMother
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt

internal class UserServiceTest {
    private val userRepositoryMock = mockk<UserRepository>()
    private val accountServiceMock = mockk<AccountService>()

    private val cut = UserService(
        userRepositoryMock,
        accountServiceMock,
    )

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Test
    fun should_ReturnUser_When_GetRegisteredUser() {
        // given
        val user = UserObjectMother.user()
        val expected = UserDto(user)
        every { userRepositoryMock.findOne(user.id) } returns user

        // when
        val actual = cut.getUser(user.id)

        // then
        actual shouldBe expected
    }

    @Test
    fun should_ThrowException_When_GetNonRegisteredUser() {
        // given
        val nonRegisteredUserId = 1L
        every { userRepositoryMock.findOne(nonRegisteredUserId) } returns null

        // when
        val executable: () -> Unit = { cut.getUser(nonRegisteredUserId) }

        // then
        shouldThrowExactly<NoDataFoundException>(executable).also {
            it.message shouldBe NoDataFoundException().message
        }
    }

    @Test
    fun should_ThrowException_When_AuthenticateWithNonRegisteredUser() {
        // given
        val authUserDto = UserObjectMother.authUser()
        every { userRepositoryMock.findOneByEmail(authUserDto.email) } returns null

        // when
        val executable: () -> Unit = { cut.authenticateUser(authUserDto) }

        // then
        shouldThrowExactly<AuthenticationException>(executable).also {
            it.message shouldBe AuthenticationException().message
        }
    }

    @Test
    fun should_ReturnUser_When_AuthenticateWithRegisteredUser() {
        // given
        val authUserDtoPasswordSlot = slot<String>()
        val userPasswordHashSlot = slot<String>()
        val authUserDto = UserObjectMother.authUser()
        val user = UserObjectMother.user()
        val expected = UserDto(user)
        mockkStatic("org.mindrot.jbcrypt.BCrypt")

        every { userRepositoryMock.findOneByEmail(authUserDto.email) } returns user
        every { BCrypt.checkpw(capture(authUserDtoPasswordSlot), capture(userPasswordHashSlot)) } returns true

        // when
        val actual = cut.authenticateUser(authUserDto)

        // then
        actual shouldBe expected
        authUserDtoPasswordSlot.captured shouldBe authUserDto.password
        userPasswordHashSlot.captured shouldBe user.passwordHash
    }

    @Test
    fun should_ThrowException_When_AuthenticateRegisteredUserWithInvalidPassword() {
        // given
        val authUserDtoPasswordSlot = slot<String>()
        val userPasswordHashSlot = slot<String>()
        val authUserDto = UserObjectMother.authUser()
        val user = UserObjectMother.user()
        mockkStatic("org.mindrot.jbcrypt.BCrypt")

        every { userRepositoryMock.findOneByEmail(authUserDto.email) } returns user
        every { BCrypt.checkpw(capture(authUserDtoPasswordSlot), capture(userPasswordHashSlot)) } returns false

        // when
        val executable: () -> Unit = { cut.authenticateUser(authUserDto) }

        // then
        shouldThrowExactly<AuthenticationException>(executable).also {
            it.message shouldBe "The password is incorrect"
        }
        authUserDtoPasswordSlot.captured shouldBe authUserDto.password
        userPasswordHashSlot.captured shouldBe user.passwordHash
    }

    @Test
    fun should_ReturnNewUser_When_CreateUser() {
        // given
        val dbTransactionSlot = slot<Transaction.() -> Any>()
        val accountCreateDtoSlot = slot<AccountCreateDto>()
        val userIdSlot = slot<Long>()

        val userCreateDto = UserObjectMother.newUser()
        val accountDto = AccountDto(AccountObjectMother.account(), userCreateDto.startBalance)
        val user = UserObjectMother.user()
        val expected = UserDto(user)
        mockkStatic("org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt")

        every { userRepositoryMock.findOneByEmail(userCreateDto.email) } returns null
        every { userRepositoryMock.createOne(userCreateDto) } returns user
        every { accountServiceMock.createAccount(capture(userIdSlot), capture(accountCreateDtoSlot)) } returns accountDto
        every { transaction(any(), capture(dbTransactionSlot)) } answers { dbTransactionSlot.invoke(mockk()) }

        // when
        val actual = cut.createUser(userCreateDto)

        // then
        actual shouldBe expected
        accountCreateDtoSlot.captured shouldBe AccountObjectMother.newAccount()
        userIdSlot.captured shouldBe user.id
    }

    @Test
    fun should_ThrowException_When_CreateExistingUser() {
        // given
        val newUserDto = UserObjectMother.newUser()
        val user = UserObjectMother.user()
        every { userRepositoryMock.findOneByEmail(newUserDto.email) } returns user

        // when
        val executable: () -> Unit = { cut.createUser(newUserDto) }

        // then
        shouldThrowExactly<DuplicateDataException>(executable).also {
            it.message shouldBe "Such email has already been taken"
        }
        verify { userRepositoryMock.createOne(any()) wasNot Called }
        verify { accountServiceMock.createAccount(any(), any()) wasNot Called }
    }

    @Test
    fun should_ReturnUpdatedUser_When_UpdateUser() {
        // given
        val userIdSlot = slot<Long>()
        val userUpdateDto = UserObjectMother.updateUser()
        val userId = UserObjectMother.user().id
        val updatedUser = UserObjectMother.user()
        val expected = UserDto(updatedUser)
        every { userRepositoryMock.updateOne(capture(userIdSlot), userUpdateDto) } returns updatedUser

        // when
        val actual = cut.updateUser(userId, userUpdateDto)

        // then
        actual shouldBe expected
        userIdSlot.captured shouldBe userId
    }

    @Test
    fun should_ReturnException_When_UpdateNonRegisteredUser() {
        // given
        val userId = 1L
        val userUpdateDto = UserObjectMother.updateUser()
        every { userRepositoryMock.updateOne(userId, userUpdateDto) } returns null

        // when
        val executable: () -> Unit = { cut.updateUser(userId, userUpdateDto) }

        // then
        shouldThrowExactly<NoDataFoundException>(executable).also {
            it.message shouldBe NoDataFoundException().message
        }
    }

    @Test
    fun should_DeleteUser_When_DeleteUser() {
        // given
        val userId = 1L
        val dbTransactionSlot = slot<Transaction.() -> Any>()
        mockkStatic("org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt")

        every { userRepositoryMock.deleteData(userId) } returns Unit
        every { userRepositoryMock.deleteOne(userId) } returns Unit
        every { transaction(any(), capture(dbTransactionSlot)) } answers { dbTransactionSlot.invoke(mockk()) }

        // when
        val actual = cut.deleteUser(userId)

        // then
        actual shouldBe Unit
        verify(exactly = 1) { userRepositoryMock.deleteData(userId) }
        verify(exactly = 1) { userRepositoryMock.deleteOne(userId) }
    }

    @Test
    fun should_DeleteUserData_When_DeleteUserData() {
        // given
        val userId = 1L
        val dbTransactionSlot = slot<Transaction.() -> Any>()
        mockkStatic("org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt")

        every { userRepositoryMock.deleteData(userId) } returns Unit
        every { transaction(any(), capture(dbTransactionSlot)) } answers { dbTransactionSlot.invoke(mockk()) }

        // when
        val actual = cut.deleteUserData(userId)

        // then
        actual shouldBe Unit
        verify(exactly = 1) { userRepositoryMock.deleteData(userId) }
    }
}
