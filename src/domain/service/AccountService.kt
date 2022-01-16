package io.ducket.api.domain.service

import io.ducket.api.CurrencyRatesClient
import io.ducket.api.domain.controller.account.*
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.record.RecordDto
import io.ducket.api.domain.repository.*
import io.ducket.api.extension.isBeforeInclusive
import io.ducket.api.extension.sumByDecimal
import io.ducket.api.plugins.DuplicateEntityError
import io.ducket.api.plugins.InvalidDataError
import io.ducket.api.plugins.NoEntityFoundError
import org.koin.java.KoinJavaComponent.inject
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Instant

class AccountService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transferRepository: TransferRepository,
    private val userRepository: UserRepository,
) {
    private val currencyRatesClient: CurrencyRatesClient by inject(CurrencyRatesClient::class.java)

    fun calculateBalance(accountOwnerId: Long, accountId: Long, beforeDate: Instant = Instant.now()): BigDecimal {
        val accountTransactions = transactionRepository.findAllByAccount(accountOwnerId, accountId).map { RecordDto(it) }
        val accountTransfers = transferRepository.findAllByAccount(accountOwnerId, accountId).map { RecordDto(it) }

        val records = accountTransactions.plus(accountTransfers)
            .sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.id })
            .filter { it.date.isBeforeInclusive(beforeDate) }

        return records.sumByDecimal { it.amount }
    }

    /**
     * Find all the user's accounts, including observed ones
     */
    fun getAccountsAccessibleToUser(userId: Long): List<AccountDto> {
        accountRepository.findAllIncludingObserved(userId).also { accounts ->
            return accounts.map { account ->
                calculateBalance(
                    accountOwnerId = account.user.id,
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
    fun getAccountDetailsAccessibleToUser(userId: Long, accountId: Long): AccountDto {
        return getAccountsAccessibleToUser(userId).firstOrNull { it.id == accountId }
            ?: throw NoEntityFoundError("No such account was found")
    }

    fun createAccount(userId: Long, reqObj: AccountCreateDto): AccountDto {
        accountRepository.findOneByName(userId, reqObj.name)?.let {
            throw DuplicateEntityError("'${reqObj.name}' account already exists")
        }

        return AccountDto(accountRepository.create(userId, reqObj))
    }

    fun updateAccount(userId: Long, accountId: Long, reqObj: AccountUpdateDto): AccountDto {
        accountRepository.findOne(userId, accountId) ?: throw NoEntityFoundError("No such account was found")

        reqObj.name?.let {
            accountRepository.findOneByName(userId, it)?.let { found ->
                if (found.id != accountId) throw InvalidDataError("'${reqObj.name}' account already exists")
            }
        }

        return accountRepository.updateOne(userId, accountId, reqObj)?.let { AccountDto(it) }
            ?: throw Exception("Cannot update account entity")
    }

    fun deleteAccount(userId: Long, accountId: Long): Boolean {
        return accountRepository.deleteOne(userId, accountId)
    }

    fun getAccountsBalance(userId: Long): AccountsBalanceDto {
        val accounts = getAccountsAccessibleToUser(userId)
        val userCurrency = userRepository.findOne(userId)?.mainCurrency
            ?: throw NoEntityFoundError("No such user was found")

        val appliedExchangeRates = mutableListOf<AccountBalanceExchangeRateDto>()

        val totalBalance = accounts.map {
            var rate = BigDecimal(1.0)
            if (it.accountCurrency.id != userCurrency.id) {
                val baseCurrency = it.accountCurrency.isoCode
                val termCurrency = userCurrency.isoCode
                rate = currencyRatesClient.getCurrencyRate(baseCurrency, termCurrency)

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