package io.ducket.api.domain.service

import io.ducket.api.app.LedgerRecordType.*
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.account.*
import io.ducket.api.domain.controller.ledger.LedgerRecordCreateDto
import io.ducket.api.domain.controller.ledger.OperationCreateDto
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.DuplicateEntityException
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import io.ducket.api.utils.lt
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.Instant

class AccountService(
    private val accountRepository: AccountRepository,
    private val ledgerService: LedgerService,
) {

    fun getAccounts(userId: Long): List<AccountDto> {
        return accountRepository.findAll(userId).map { AccountDto(it) }
    }
    
    fun getAccount(userId: Long, accountId: Long): AccountDto {
        return getAccounts(userId).firstOrNull { it.id == accountId } ?: throw NoEntityFoundException()
    }

    fun createAccount(userId: Long, payload: AccountCreateDto): AccountDto {
        accountRepository.findOneByName(userId, payload.name)?.let { throw DuplicateEntityException() }

        return transaction {
            accountRepository.create(userId, payload).let { newAccount ->
                if (payload.startBalance != BigDecimal.ZERO) {
                    ledgerService.createLedgerRecord(
                        userId = userId,
                        payload = LedgerRecordCreateDto(
                            amount = payload.startBalance,
                            type = if (payload.startBalance.lt(BigDecimal.ZERO)) EXPENSE else INCOME,
                            accountId = newAccount.id,
                            operation = OperationCreateDto(
                                category = "Other",
                                categoryGroup = "Other",
                                description = "Starting balance",
                                date = Instant.now(),
                            )
                        )
                    )
                    return@transaction AccountDto(newAccount.copy(balance = payload.startBalance))
                }
                return@transaction AccountDto(newAccount)
            }
        }
    }

    fun updateAccount(userId: Long, accountId: Long, payload: AccountUpdateDto): AccountDto {
        payload.name?.also {
            accountRepository.findOneByName(userId, it)?.let { found ->
                if (found.id != accountId) throw InvalidDataException("Account with such name already exists")
            }
        }

        return accountRepository.updateOne(userId, accountId, payload)?.let { AccountDto(it) } ?: throw NoEntityFoundException()
    }

    fun deleteAccounts(userId: Long, payload: BulkDeleteDto) {
        accountRepository.delete(userId, *payload.ids.toLongArray())
    }

    fun deleteAccount(userId: Long, accountId: Long) {
        accountRepository.delete(userId, accountId)
    }
}