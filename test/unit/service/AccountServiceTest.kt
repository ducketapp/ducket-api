package dev.ducket.api.unit.service

import dev.ducket.api.BaseUnitTest
import dev.ducket.api.domain.controller.BulkDeleteDto
import dev.ducket.api.domain.model.account.AccountCreate
import dev.ducket.api.domain.model.account.AccountUpdate
import dev.ducket.api.domain.repository.AccountRepository
import dev.ducket.api.domain.service.AccountService
import dev.ducket.api.plugins.DuplicateDataException
import dev.ducket.api.plugins.NoDataFoundException
import dev.ducket.api.test_data.AccountObjectMother
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class AccountServiceTest : BaseUnitTest() {
    private val accountRepositoryMock = mockk<AccountRepository>()
    private val cut = AccountService(accountRepositoryMock)

    @Test
    fun should_ReturnAccounts_When_GetAccounts() {
        // given
        val userIdSlot = slot<Long>()
        coEvery { accountRepositoryMock.findAll(capture(userIdSlot)) } returns listOf(AccountObjectMother.account())

        // when
        val actual = runBlocking { cut.getAccounts(userId) }

        // then
        actual shouldBe listOf(AccountObjectMother.accountDto())
        userIdSlot.captured shouldBe userId
        coVerify(exactly = 1) { accountRepositoryMock.findAll(userId) }
    }

    @Test
    fun should_ReturnAccount_When_GetAccount() {
        // given
        val cutSpy = spyk(cut)
        val userIdSlot = slot<Long>()
        val expected = AccountObjectMother.accountDto()

        coEvery { cutSpy.getAccounts(capture(userIdSlot)) } returns listOf(expected, expected.copy(id = 2L))

        // when
        val actual = runBlocking { cutSpy.getAccount(userId, expected.id) }

        // then
        actual shouldBe expected
        userIdSlot.captured shouldBe userId
        coVerify(exactly = 1) { cutSpy.getAccounts(userId) }
    }

    @Test
    fun should_ThrowException_When_GetNonExistentAccount() {
        // given
        val cutSpy = spyk(cut)
        val userIdSlot = slot<Long>()

        coEvery { cutSpy.getAccounts(capture(userIdSlot)) } returns listOf(AccountObjectMother.accountDto())

        // when
        val executable = { runBlocking { cutSpy.getAccount(userId, 123L) } }

        // then
        shouldThrowExactly<NoDataFoundException>(executable).also {
            it.message shouldBe NoDataFoundException().message
        }
        userIdSlot.captured shouldBe userId
        coVerify(exactly = 1) { cutSpy.getAccounts(userId) }
    }

    @Test
    fun should_ReturnAccount_When_CreateAccount() {
        // given
        val userIdSlot = slot<Long>()
        val accountCreateSlot = slot<AccountCreate>()

        val account = AccountObjectMother.account()
        val accountCreateDto = AccountObjectMother.accountCreateDto()
        val expected = AccountObjectMother.accountDto()

        coEvery { accountRepositoryMock.findOneByTitle(userId, accountCreateDto.name) } returns null
        coEvery { accountRepositoryMock.create(capture(userIdSlot), capture(accountCreateSlot)) } returns account

        // when
        val actual = runBlocking { cut.createAccount(userId, accountCreateDto) }

        // then
        actual shouldBe expected
        userIdSlot.captured shouldBe userId
        accountCreateSlot.captured shouldBe AccountObjectMother.accountCreate()
    }

    @Test
    fun should_ThrowException_When_CreateExistingAccount() {
        // given
        val account = AccountObjectMother.account()
        val accountCreateDto = AccountObjectMother.accountCreateDto()
        coEvery { accountRepositoryMock.findOneByTitle(userId, accountCreateDto.name) } returns account

        // when
        val executable = { runBlocking { cut.createAccount(userId, accountCreateDto) } }

        // then
        shouldThrowExactly<DuplicateDataException>(executable).also {
            it.message shouldBe DuplicateDataException().message
        }
        coVerify { accountRepositoryMock.create(any(), any()) wasNot Called }
    }

    @Test
    fun should_ReturnAccount_When_UpdateExistingAccount() {
        // given
        val userIdSlot = slot<Long>()
        val accountIdSlot = slot<Long>()
        val accountUpdateSlot = slot<AccountUpdate>()

        val account = AccountObjectMother.account()
        val accountUpdateDto = AccountObjectMother.accountUpdateDto()

        coEvery { accountRepositoryMock.findOneByTitle(userId, accountUpdateDto.name) } returns null
        coEvery { accountRepositoryMock.update(capture(userIdSlot), capture(accountIdSlot), capture(accountUpdateSlot)) } returns account

        // when
        val actual = runBlocking { cut.updateAccount(userId, account.id, accountUpdateDto) }

        // then
        actual shouldBe AccountObjectMother.accountDto()
        userIdSlot.captured shouldBe userId
        accountIdSlot.captured shouldBe account.id
        accountUpdateSlot.captured shouldBe AccountObjectMother.accountUpdate()
    }

    @Test
    fun should_ThrowException_When_UpdateAccountWithExistingAccountTitle() {
        // given
        val accountUpdateDto = AccountObjectMother.accountUpdateDto()
        val account = AccountObjectMother.account().copy(id = 2L, name = accountUpdateDto.name)

        coEvery { accountRepositoryMock.findOneByTitle(userId, accountUpdateDto.name) } returns account

        // when
        val executable = { runBlocking { cut.updateAccount(userId, 1L, accountUpdateDto) } }

        // then
        shouldThrowExactly<DuplicateDataException>(executable).also {
            it.message shouldBe DuplicateDataException().message
        }
        coVerify { accountRepositoryMock.update(any(), any(), any())?.wasNot(Called) }
    }

    @Test
    fun should_ThrowException_When_UpdateNonExistentAccount() {
        // given
        val userIdSlot = slot<Long>()
        val accountIdSlot = slot<Long>()
        val accountUpdateSlot = slot<AccountUpdate>()

        val account = AccountObjectMother.account()
        val accountUpdateDto = AccountObjectMother.accountUpdateDto()

        coEvery { accountRepositoryMock.findOneByTitle(userId, accountUpdateDto.name) } returns null
        coEvery { accountRepositoryMock.update(capture(userIdSlot), capture(accountIdSlot), capture(accountUpdateSlot)) } returns null

        // when
        val executable = { runBlocking { cut.updateAccount(userId, account.id, accountUpdateDto) } }

        // then
        shouldThrowExactly<NoDataFoundException>(executable).also {
            it.message shouldBe NoDataFoundException().message
        }
        userIdSlot.captured shouldBe userId
        accountIdSlot.captured shouldBe account.id
        accountUpdateSlot.captured shouldBe AccountObjectMother.accountUpdate()
    }

    @Test
    fun should_DeleteAccount_When_DeleteAccount() {
        // given
        val userIdSlot = slot<Long>()
        val accountIdSlot = slot<Long>()
        val account = AccountObjectMother.account()

        coEvery { accountRepositoryMock.delete(capture(userIdSlot), capture(accountIdSlot)) } returns Unit

        // when
        val actual = runBlocking { cut.deleteAccount(userId, account.id) }

        // then
        actual shouldBe Unit
        userIdSlot.captured shouldBe userId
        accountIdSlot.captured shouldBe account.id
    }

    @Test
    fun should_DeleteAccounts_When_DeleteAccounts() {
        // given
        val userIdSlot = slot<Long>()
        val accountIdSlot = slot<Long>()
        val account = AccountObjectMother.account()

        coEvery { accountRepositoryMock.delete(capture(userIdSlot), capture(accountIdSlot)) } returns Unit

        // when
        val actual = runBlocking { cut.deleteAccounts(userId, BulkDeleteDto(ids = listOf(account.id))) }

        // then
        actual shouldBe Unit
        userIdSlot.captured shouldBe userId
        accountIdSlot.captured shouldBe account.id
    }
}