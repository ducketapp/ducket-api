package io.ducket.api.domain.service

import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.account.AccountUpdateDto
import io.ducket.api.domain.controller.record.RecordDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.CurrencyRepository
import io.ducket.api.domain.repository.TransactionRepository
import io.ducket.api.domain.repository.TransferRepository
import io.ducket.api.extension.isBeforeInclusive
import io.ducket.api.extension.sumByDecimal
import io.ducket.api.plugins.DuplicateEntityError
import io.ducket.api.plugins.InvalidDataError
import io.ducket.api.plugins.NoEntityFoundError
import java.math.BigDecimal
import java.time.Instant

class AccountService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transferRepository: TransferRepository,
) {

    fun resolveBalance(userId: String, accountId: String, beforeDate: Instant = Instant.now()): BigDecimal {
        val transactions = transactionRepository.findAllByAccount(userId, accountId)
            .filter { it.date.isBeforeInclusive(beforeDate) }.map { RecordDto(it) }

        val transfers = transferRepository.findAllByAccount(userId, accountId)
            .filter { it.date.isBeforeInclusive(beforeDate) }.map { RecordDto(it) }

        val records = transactions.plus(transfers)
            .sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.amount })

        return records.sumByDecimal { it.amount }
    }

    fun getAccounts(userId: String): List<AccountDto> {
        accountRepository.findAll(userId).also { accounts ->
            return accounts.map {
                val totalBalance = resolveBalance(userId, it.id)
                AccountDto(it, totalBalance)
            }
        }
    }

    fun getAccountDetails(userId: String, accountId: String): AccountDto {
        return getAccounts(userId).firstOrNull { it.id == accountId }
            ?: throw NoEntityFoundError("No such account was found")
    }

    fun createAccount(userId: String, reqObj: AccountCreateDto): AccountDto {
        accountRepository.findOneByName(userId, reqObj.name)?.let {
            throw DuplicateEntityError("'${reqObj.name}' account already exists")
        }

        return AccountDto(accountRepository.create(userId, reqObj))
    }

    fun updateAccount(userId: String, accountId: String, reqObj: AccountUpdateDto): AccountDto {
        accountRepository.findOne(userId, accountId) ?: throw NoEntityFoundError("No such account was found")

        reqObj.name?.let {
            accountRepository.findOneByName(userId, it)?.let { found ->
                if (found.id != accountId) throw InvalidDataError("'${reqObj.name}' account already exists")
            }
        }

        return accountRepository.updateOne(userId, accountId, reqObj)?.let { AccountDto(it) }
            ?: throw Exception("Cannot update account entity")
    }

    fun deleteAccount(userId: String, accountId: String): Boolean {
        return accountRepository.deleteOne(userId, accountId)
    }
}