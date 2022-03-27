package io.ducket.api.domain.service

import domain.model.account.AccountEntity
import domain.model.category.CategoriesTable
import domain.model.category.CategoryEntity
import io.ducket.api.CurrencyRateProvider
import io.ducket.api.app.CategoryGroup
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.account.*
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.record.RecordDto
import io.ducket.api.domain.controller.transaction.TransactionCreateDto
import io.ducket.api.domain.repository.*
import io.ducket.api.extension.eq
import io.ducket.api.extension.gt
import io.ducket.api.extension.isBeforeInclusive
import io.ducket.api.extension.sumByDecimal
import io.ducket.api.plugins.DuplicateEntityException
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.java.KoinJavaComponent.inject
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Instant

class AccountService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transferRepository: TransferRepository,
    private val userRepository: UserRepository,
    private val groupService: GroupService,
) {
    private val currencyRateProvider: CurrencyRateProvider by inject(CurrencyRateProvider::class.java)

    fun calculateBalance(ownerId: Long, accountId: Long, beforeDate: Instant = Instant.now()): BigDecimal {
        val accountTransactions = transactionRepository.findAllByAccount(ownerId, accountId).map { RecordDto(it) }
        val accountTransfers = transferRepository.findAllByAccount(ownerId, accountId).map { RecordDto(it) }

        val records = accountTransactions.plus(accountTransfers)
            .sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.id })
            .filter { it.date.isBeforeInclusive(beforeDate) }

        return records.sumByDecimal { it.amount }
    }

    /**
     * Find all the user's accounts, including observed ones
     */
    fun getAccountsAccessibleToUser(userId: Long): List<AccountDto> {
        val userIds = groupService.getDistinctUsersWithMutualGroupMemberships(userId).map { it.id } + userId

        accountRepository.findAll(*userIds.toLongArray()).also { accounts ->
            return accounts.map { account ->
                calculateBalance(
                    ownerId = account.user.id,
                    accountId = account.id,
                ).let {
                    AccountDto(account, it)
                }
            }
        }
    }

    /**
     * Find the user's account among all the accounts, including observed ones
     */
    fun getAccountAccessibleToUser(userId: Long, accountId: Long): AccountDto {
        return getAccountsAccessibleToUser(userId).firstOrNull { it.id == accountId } ?: throw NoEntityFoundException()
    }

    fun createAccount(userId: Long, payload: AccountCreateDto): AccountDto {
        accountRepository.findOneByName(userId, payload.name)?.let {
            throw DuplicateEntityException()
        }

        return transaction {
            accountRepository.create(userId, payload).let { newAccount ->
                if (payload.startBalance.gt(BigDecimal.ZERO)) {
                    transactionRepository.create(
                        userId = userId,
                        dto = TransactionCreateDto(
                            amount = payload.startBalance,
                            accountId = newAccount.id,
                            categoryId = CategoryEntity.find { CategoriesTable.name.eq(CategoryGroup.OTHER.name) }.first().id.value,
                            notes = "Corrective transaction",
                            date = Instant.now(),
                        )
                    )
                }

                return@transaction AccountDto(AccountEntity[newAccount.id].toModel())
            }
        }
    }

    fun updateAccount(userId: Long, accountId: Long, payload: AccountUpdateDto): AccountDto {
        payload.name?.also {
            accountRepository.findOneByName(userId, it)?.let { found ->
                if (found.id != accountId) throw InvalidDataException()
            }
        }

        val updatedAccount = accountRepository.updateOne(userId, accountId, payload) ?: throw NoEntityFoundException()
        return AccountDto(updatedAccount)
    }

    fun deleteAccounts(userId: Long, payload: BulkDeleteDto) {
        accountRepository.delete(userId, *payload.ids.toLongArray())
    }

    fun deleteAccount(userId: Long, accountId: Long) {
        accountRepository.delete(userId, accountId)
    }

    fun getAccountsBalance(userId: Long): AccountsBalanceDto {
        val accounts = getAccountsAccessibleToUser(userId)
        val userCurrency = userRepository.findOne(userId)?.mainCurrency
            ?: throw NoEntityFoundException("No such user was found")

        val appliedExchangeRates = mutableListOf<AccountBalanceExchangeRateDto>()

        val totalBalance = accounts.map {
            var rate = BigDecimal(1.0)
            if (it.accountCurrency.id != userCurrency.id) {
                val baseCurrency = it.accountCurrency.isoCode
                val termCurrency = userCurrency.isoCode
                rate = currencyRateProvider.getCurrencyRate(baseCurrency, termCurrency)

                appliedExchangeRates.add(AccountBalanceExchangeRateDto(baseCurrency, termCurrency, rate))
            }
            return@map it.balance * rate
        }.sumByDecimal { it }

        val totalBalanceFormatter = DecimalFormat("#,##0.00")

        return AccountsBalanceDto(
            totalBalance = totalBalanceFormatter.format(totalBalance),
            currency = CurrencyDto(userCurrency),
            appliedExchangeRates = appliedExchangeRates,
            accounts = accounts
        )
    }
}