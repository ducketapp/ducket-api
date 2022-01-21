package io.ducket.api

import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.repository.*
import io.ducket.api.domain.service.UserService
import io.ducket.api.plugins.AuthenticationException
import io.ducket.api.plugins.DuplicateEntityException
import io.ducket.api.plugins.NoEntityFoundException
import io.ducket.api.test_data.AccountObjectMother
import io.ducket.api.test_data.UserObjectMother
import io.mockk.*
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class UserServiceTest {

    private val userRepositoryMock: UserRepository = mockk()
    private val accountRepositoryMock: AccountRepository = mockk()
    private val followRepositoryMock: FollowRepository = mockk()
    private val transactionRepository: TransactionRepository = mockk()
    private val transferRepository: TransferRepository = mockk()
    private val budgetRepository: BudgetRepository = mockk()
    private val importRuleRepository: ImportRuleRepository = mockk()
    private val importRepository: ImportRepository = mockk()

    private val cut = UserService(
        userRepositoryMock,
        accountRepositoryMock,
        followRepositoryMock,
        transactionRepository,
        transferRepository,
        budgetRepository,
        importRuleRepository,
        importRepository,
    )

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Test
    fun should_ReturnNewUser_When_CreateUser() {
        // given
        val dbTransactionSlot = slot<Transaction.() -> Any>()
        val accountSlot = slot<AccountCreateDto>()

        val newUserDto = UserObjectMother.newJohnWick()
        val user = UserObjectMother.johnWick()
        val account = AccountObjectMother.cashUsd()
        val expected = UserDto(user)

        mockkStatic("org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt")

        every { userRepositoryMock.findOneByEmail(newUserDto.email) } returns null
        every { userRepositoryMock.create(newUserDto) } returns user
        every { accountRepositoryMock.create(user.id, capture(accountSlot)) } returns account
        every { transaction(any(), capture(dbTransactionSlot)) } answers { dbTransactionSlot.invoke(mockk()) }

        // when
        val actual = cut.setupNewUser(newUserDto)

        // then
        assertEquals(expected, actual)
        assertEquals(AccountObjectMother.newCashUsd(), accountSlot.captured)
    }

    @Test
    fun should_ThrowDuplicateEntityException_When_CreateExistingUser() {
        // given
        val newUserDto = UserObjectMother.newJohnWick()
        val user = UserObjectMother.default()
        every { userRepositoryMock.findOneByEmail(newUserDto.email) } returns user

        // when
        val executable: () -> Unit = { cut.setupNewUser(newUserDto) }

        // then
        assertFailsWith(DuplicateEntityException::class, executable)
        verify { userRepositoryMock.create(any()) wasNot Called }
        verify { accountRepositoryMock.create(any(), any()) wasNot Called }
    }

    @Test
    fun should_ThrowNoEntityFoundException_When_GetNonRegisteredUser() {
        // given
        val nonRegisteredUserId = 1L
        every { userRepositoryMock.findOne(nonRegisteredUserId) } returns null

        // when
        val executable: () -> Unit = { cut.getUser(nonRegisteredUserId) }

        // then
        assertFailsWith(NoEntityFoundException::class, executable)
    }

    @Test
    fun should_ReturnUser_When_GetRegisteredUser() {
        // given
        val user = UserObjectMother.johnWick()
        val expected = UserDto(user)
        every { userRepositoryMock.findOne(user.id) } returns user

        // when
        val actual = cut.getUser(user.id)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun should_ThrowAuthenticationException_When_AuthenticateNonRegisteredUser() {
        // given
        val authUserDto = UserObjectMother.authJohnWick()
        every { userRepositoryMock.findOneByEmail(authUserDto.email) } returns null

        // when
        val executable: () -> Unit = { cut.authenticateUser(authUserDto) }

        // then
        assertFailsWith(AuthenticationException::class, executable)
    }

    @Test
    fun should_ReturnUser_When_AuthenticateValidRegisteredUser() {
        // given
        val authUserDto = UserObjectMother.authJohnWick()
        val user = UserObjectMother.johnWick()
        val expected = UserDto(user)
        mockkStatic("org.mindrot.jbcrypt.BCrypt")

        every { userRepositoryMock.findOneByEmail(authUserDto.email) } returns user
        every { BCrypt.checkpw(any(), any()) } returns true

        // when
        val actual = cut.authenticateUser(authUserDto)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun should_ThrowAuthenticationException_When_AuthenticateRegisteredUserWithInvalidPassword() {
        // given
        val authUserDto = UserObjectMother.authJohnWick()
        val user = UserObjectMother.johnWick()
        mockkStatic("org.mindrot.jbcrypt.BCrypt")

        every { userRepositoryMock.findOneByEmail(authUserDto.email) } returns user
        every { BCrypt.checkpw(any(), any()) } returns false

        // when
        val executable: () -> Unit = { cut.authenticateUser(authUserDto) }

        // then
        assertFailsWith(AuthenticationException::class, executable)
    }
}
