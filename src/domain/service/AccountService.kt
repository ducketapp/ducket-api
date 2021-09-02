package io.budgery.api.domain.service

import io.budgery.api.domain.controller.account.AccountCreateDto
import io.budgery.api.domain.controller.account.AccountDto
import io.budgery.api.domain.controller.account.AccountUpdateDto
import io.budgery.api.domain.controller.record.TransactionDto
import io.budgery.api.domain.controller.record.TransferDto
import io.budgery.api.domain.repository.AccountRepository
import io.budgery.api.domain.repository.TransactionRepository
import io.budgery.api.domain.repository.TransferRepository
import io.budgery.api.extension.isBeforeInclusive
import io.budgery.api.extension.sumByDecimal
import java.math.BigDecimal
import java.time.Instant

class AccountService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transferRepository: TransferRepository,
) {

    fun getAmount(userId: Int, accountId: Int, beforeDate: Instant = Instant.now()): BigDecimal {
        val transactionsAmount = transactionRepository.findAllByAccount(userId, accountId)
            .map { TransactionDto(it) }
            .filter { it.date.isBeforeInclusive(beforeDate) }
            .sumByDecimal { it.amount }

        val transfersAmount = transferRepository.findAllByAccount(userId, accountId)
            .map { TransferDto(it) }
            .filter { it.date.isBeforeInclusive(beforeDate) }
            .sumByDecimal { it.amount }

        return transactionsAmount + transfersAmount
    }

    fun getAccounts(userId: Int) : List<AccountDto> {
        accountRepository.findAll(userId).also { list ->
            return list.map {
                val numOfTransactions = transactionRepository.getTotalByAccount(userId, it.id)
                val numOfTransfers = transferRepository.getTotalByAccount(userId, it.id)
                val totalAmount = getAmount(userId, it.id)

                AccountDto(it, totalAmount, numOfTransactions + numOfTransfers)
            }
        }
    }

    fun getAccount(userId: Int, accountId: Int) : AccountDto {
        return getAccounts(userId).firstOrNull { it.account.id == accountId }
            ?: throw NoSuchElementException("No such account was found")
    }

    fun createAccount(userId: Int, reqObj: AccountCreateDto) : AccountDto {
        accountRepository.findOneByName(userId, reqObj.name)?.let {
            throw IllegalArgumentException("'${reqObj.name}' account already exists")
        }

        val newAccount = accountRepository.create(userId, reqObj)

        return AccountDto(newAccount, BigDecimal.ZERO, 0)
    }

    fun updateAccount(userId: Int, accountId: Int, reqObj: AccountUpdateDto) : AccountDto {
        accountRepository.findOne(userId, accountId) ?: throw NoSuchElementException("No such account was found")

        reqObj.name?.let {
            accountRepository.findOneByName(userId, it)?.let { found ->
                if (found.id != accountId) throw IllegalArgumentException("'${reqObj.name}' account already exists")
            }
        }

        return accountRepository.updateOne(userId, accountId, reqObj)?.let {
            AccountDto(it)
        } ?: throw Exception("No such account was found")
    }

    fun deleteAccount(userId: Int, accountId: Int) : Boolean {
        return accountRepository.deleteOne(userId, accountId)
    }

/*    fun deleteAccounts(userId: Int, payload: AccountDeleteDTO): List<AccountDTO> {
        if (!accountRepository.getAllByUserId(userId).map { it.id }.containsAll(payload.accountIds)) {
            throw NoSuchElementException()
        }

        payload.accountIds.forEach {
            accountRepository.deleteById(it, userId)
        }

        return getAccounts(userId)
    }*/
}