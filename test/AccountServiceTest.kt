package io.ducket.api

import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.LedgerRepository
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.GroupService
import io.ducket.api.domain.service.LedgerService
import io.ducket.api.plugins.NoEntityFoundException
import io.ducket.api.test_data.AccountObjectMother
import io.ducket.api.test_data.UserObjectMother
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class AccountServiceTest {
    private val accountRepositoryMock = mockk<AccountRepository>()
//    private val transactionRepositoryMock = mockk<TransactionRepository>()
//    private val transferRepositoryMock = mockk<TransferRepository>()
    private val ledgerRepositoryMock = mockk<LedgerRepository>()
    private val ledgerServiceMock = mockk<LedgerService>()
    private val groupServiceMock = mockk<GroupService>()

    private val cut = AccountService(
        accountRepositoryMock,
        ledgerServiceMock,
    )

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

//    @Test
//    fun should_ReturnAccessibleUserAccounts_When_GetUserAccounts() {
//        // given
//        val accountServiceSpy = spyk(cut)
//        val userIdSlot = slot<Long>()
//        val sharedUserIdSlot = slot<Long>()
//
//        val user = UserObjectMother.user()
//        val sharedUser = UserObjectMother.user()
//        val accounts = listOf(
//            AccountObjectMother.account().copy(user = user),
//            AccountObjectMother.account().copy(user = sharedUser),
//        )
//        val expected = accounts.map { AccountDto(it) }
//
//        every { groupServiceMock.getActiveMembersFromSharedUserGroups(user.id) } returns listOf(UserDto(sharedUser))
//        every { accountRepositoryMock.findAll(capture(sharedUserIdSlot), capture(userIdSlot)) } returns accounts
//        every { accountServiceSpy.resolveAccountBalance(user.id, accounts[0].id, any()) } returns expected[0].balance
//        every { accountServiceSpy.resolveAccountBalance(sharedUser.id, accounts[1].id, any()) } returns expected[1].balance
//
//        // when
//        val actual = accountServiceSpy.getAccounts(user.id)
//
//        // then
//        actual shouldBe expected
//        sharedUserIdSlot.captured shouldBe sharedUser.id
//        userIdSlot.captured shouldBe user.id
//        verify(exactly = 2) { accountServiceSpy.resolveAccountBalance(any(), any(), any()) }
//    }

    @Test
    fun should_ReturnAccessibleUserAccount_When_GetUserAccount() {
        // given
        val accountServiceSpy = spyk(cut)
        val userIdSlot = slot<Long>()
        val user = UserObjectMother.user()
        val accounts = listOf(AccountObjectMother.account().copy(user = user))
        val expected = accounts.map { AccountDto(it) }[0]

        every { accountServiceSpy.getAccounts(capture(userIdSlot)) } returns accounts.map { AccountDto(it) }

        // when
        val actual = accountServiceSpy.getAccount(user.id, accounts[0].id)

        // then
        actual shouldBe expected
        userIdSlot.captured shouldBe user.id
        verify(exactly = 1) { accountServiceSpy.getAccounts(user.id) }
    }

    @Test
    fun should_ThrowException_When_GetNonexistentUserAccount() {
        // given
        val accountServiceSpy = spyk(cut)
        val userIdSlot = slot<Long>()
        val user = UserObjectMother.user()
        val accounts = listOf(AccountObjectMother.account().copy(user = user))

        every { accountServiceSpy.getAccounts(capture(userIdSlot)) } returns accounts.map { AccountDto(it) }

        // when
        val executable: () -> Unit = { accountServiceSpy.getAccount(user.id, 10L) }

        // then
        shouldThrowExactly<NoEntityFoundException>(executable).also {
            it.message shouldBe NoEntityFoundException().message
        }
        userIdSlot.captured shouldBe user.id
        verify(exactly = 1) { accountServiceSpy.getAccounts(user.id) }
    }

//    @Test
//    fun should_ReturnNewAccount_When_CreateAccount() {
//        // given
//        val dbTransactionSlot = slot<Transaction.() -> Any>()
//        val userIdSlot = slot<Long>()
//        val accountCreateDtoSlot = slot<AccountCreateDto>()
//
//        val user = UserObjectMother.user()
//        val account = AccountObjectMother.account()
//        val accountCreateDto = AccountObjectMother.newAccount()
//        val expected = AccountDto(account)
//        mockkStatic("org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt")
//
//        every { accountRepositoryMock.findOneByName(user.id, accountCreateDto.name) } returns null
//        every { accountRepositoryMock.create(capture(userIdSlot), capture(accountCreateDtoSlot)) } returns account
//        every { transaction(any(), capture(dbTransactionSlot)) } answers { dbTransactionSlot.invoke(mockk()) }
//
//        // when
//        val actual = cut.createAccount(user.id, accountCreateDto)
//
//        // then
//        actual shouldBe expected
//        userIdSlot.captured shouldBe user.id
//        accountCreateDtoSlot.captured shouldBe accountCreateDto
//        verify { transactionRepositoryMock.create(any(), any()) wasNot Called }
//    }

//    @Test
//    fun should_ReturnNewAccount_When_CreateAccountWithStartBalance() {
//        // given
//        val dbTransactionSlot = slot<Transaction.() -> Any>()
//        val userIdSlot = slot<Long>()
//        val accountCreateDtoSlot = slot<AccountCreateDto>()
//        val transactionCreateDtoSlot = slot<TransactionCreateDto>()
//
//        val startBalance = BigDecimal(10.0)
//        val user = UserObjectMother.user()
//        val transaction = TransactionObjectMother.transaction().copy(amount = startBalance)
//        val transactionCreateDto = TransactionObjectMother.newCorrectiveTransaction(amount = startBalance)
//        val account = AccountObjectMother.account()
//        val accountCreateDto = AccountObjectMother.newAccount().copy(startBalance = startBalance)
//        val expected = AccountDto(account).copy(balance = startBalance, recordsCount = 1)
//        mockkStatic("org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt")
//
//        every { accountRepositoryMock.findOneByName(user.id, accountCreateDto.name) } returns null
//        every { accountRepositoryMock.create(capture(userIdSlot), capture(accountCreateDtoSlot)) } returns account
//        every { transactionRepositoryMock.create(capture(userIdSlot), capture(transactionCreateDtoSlot)) } returns transaction
//        every { transaction(any(), capture(dbTransactionSlot)) } answers { dbTransactionSlot.invoke(mockk()) }
//
//        // when
//        val actual = cut.createAccount(user.id, accountCreateDto)
//
//        // then
//        actual shouldBe expected
//        userIdSlot.captured shouldBe user.id
//        accountCreateDtoSlot.captured shouldBe accountCreateDto
//        transactionCreateDtoSlot.captured.shouldBeEqualToIgnoringFields(transactionCreateDto, TransactionCreateDto::date)
//        verify(exactly = 1) { transactionRepositoryMock.create(any(), any()) }
//    }

//    @Test
//    fun should_ThrowException_When_CreateExistingAccount() {
//        // given
//        val user = UserObjectMother.user()
//        val account = AccountObjectMother.account()
//        val accountCreateDto = AccountObjectMother.newAccount()
//
//        every { accountRepositoryMock.findOneByName(user.id, accountCreateDto.name) } returns account
//
//        // when
//        val executable: () -> Unit = { cut.createAccount(user.id, accountCreateDto) }
//
//        // then
//        shouldThrowExactly<DuplicateEntityException>(executable).also {
//            it.message shouldBe DuplicateEntityException().message
//        }
//        verify { accountRepositoryMock.create(any(), any()) wasNot Called }
//        verify { transactionRepositoryMock.create(any(), any()) wasNot Called }
//    }
}