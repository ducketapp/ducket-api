package io.ducket.api.domain.service

import io.ducket.api.app.LedgerRecordType.*
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.account.*
import io.ducket.api.domain.controller.ledger.LedgerRecordCreateDto
import io.ducket.api.domain.controller.ledger.OperationCreateDto
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoDataFoundException
import io.ducket.api.utils.lt
import java.math.BigDecimal
import java.time.Instant

class AccountService(
    private val accountRepository: AccountRepository,
    private val ledgerService: LedgerService,
): Transactional {

    fun getAccounts(userId: Long): List<AccountDto> {
        return accountRepository.findAll(userId).map { AccountDto(it) }
    }
    
    fun getAccount(userId: Long, accountId: Long): AccountDto {
        return getAccounts(userId).firstOrNull { it.id == accountId } ?: throw NoDataFoundException()
    }

    suspend fun createAccount(userId: Long, reqObj: AccountCreateDto): AccountDto {
        accountRepository.findOneByName(userId, reqObj.name)?.let { throw DuplicateDataException() }

        return blockingTransaction {
            accountRepository.create(userId, reqObj).let { newAccount ->
                if (reqObj.startBalance != BigDecimal.ZERO) {
                    ledgerService.createLedgerRecord(
                        userId = userId,
                        reqObj = LedgerRecordCreateDto(
                            amount = reqObj.startBalance,
                            type = if (reqObj.startBalance.lt(BigDecimal.ZERO)) EXPENSE else INCOME,
                            accountId = newAccount.id,
                            operation = OperationCreateDto(
                                category = "Other",
                                categoryGroup = "Other",
                                description = "Starting balance",
                                date = Instant.now(),
                            )
                        )
                    )
                    return@blockingTransaction AccountDto(newAccount.copy(balance = reqObj.startBalance))
                }
                return@blockingTransaction AccountDto(newAccount)
            }
        }
    }

    fun updateAccount(userId: Long, accountId: Long, payload: AccountUpdateDto): AccountDto {
        payload.name?.also {
            accountRepository.findOneByName(userId, it)?.let { found ->
                if (found.id != accountId) throw InvalidDataException("Account with such name already exists")
            }
        }

        return accountRepository.updateOne(userId, accountId, payload)?.let { AccountDto(it) } ?: throw NoDataFoundException()
    }

    fun deleteAccounts(userId: Long, payload: BulkDeleteDto) {
        accountRepository.delete(userId, *payload.ids.toLongArray())
    }

    fun deleteAccount(userId: Long, accountId: Long) {
        accountRepository.delete(userId, accountId)
    }
}