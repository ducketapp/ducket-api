package io.ducket.api.domain.service

import io.ducket.api.app.OperationType
import io.ducket.api.app.database.Transactional
import domain.mapper.AccountMapper
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.account.dto.AccountUpdateDto
import io.ducket.api.domain.controller.operation.dto.OperationCreateUpdateDto
import io.ducket.api.domain.controller.operation.dto.OperationAmountDto
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException
import java.math.BigDecimal
import java.time.Instant

class AccountService(
    private val accountRepository: AccountRepository,
    private val operationService: OperationService,
    private val categoryRepository: CategoryRepository
): Transactional {

    suspend fun getAccounts(userId: Long): List<AccountDto> {
        return accountRepository.findAll(userId).map { AccountMapper.mapModelToDto(it) }
    }
    
    suspend fun getAccount(userId: Long, accountId: Long): AccountDto {
        return getAccounts(userId).firstOrNull { it.id == accountId } ?: throw NoDataFoundException()
    }

    suspend fun createAccount(userId: Long, dto: AccountCreateDto): AccountDto {
        accountRepository.findOneByName(userId, dto.name)?.let { throw DuplicateDataException() }

        return blockingTransaction {
            accountRepository.create(userId, AccountMapper.mapDtoToModel(dto, userId)).let { account ->
                if (dto.startBalance != BigDecimal.ZERO) {
                    val categoryOther = categoryRepository.findOneByName("Other")!!

                    operationService.createOperation(
                        userId = userId,
                        dto = OperationCreateUpdateDto(
                            accountId = account.id,
                            amountData = OperationAmountDto(
                                cleared = dto.startBalance,
                                posted = dto.startBalance,
                            ),
                            categoryId = categoryOther.id,
                            type = if (dto.startBalance < BigDecimal.ZERO) OperationType.EXPENSE else OperationType.INCOME,
                            description = "Balance correction",
                            notes = "Starting balance",
                            date = Instant.now(),
                        )
                    )
                    AccountMapper.mapModelToDto(account.copy(balance = dto.startBalance))
                }
                AccountMapper.mapModelToDto(account)
            }
        }
    }

    suspend fun updateAccount(userId: Long, accountId: Long, dto: AccountUpdateDto): AccountDto {
        accountRepository.findOneByName(userId, dto.name)?.takeIf { it.id != accountId }?.also { throw DuplicateDataException() }

        return accountRepository.update(userId, accountId, AccountMapper.mapDtoToModel(dto))?.let {
            AccountMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun deleteAccounts(userId: Long, dto: BulkDeleteDto) {
        accountRepository.delete(userId, *dto.ids.toLongArray())
    }

    suspend fun deleteAccount(userId: Long, accountId: Long) {
        accountRepository.delete(userId, accountId)
    }
}