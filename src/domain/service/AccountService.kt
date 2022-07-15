package io.ducket.api.domain.service

import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.account.dto.AccountUpdateDto
import io.ducket.api.domain.mapper.AccountMapper.toDto
import io.ducket.api.domain.mapper.AccountMapper.toModel
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException

class AccountService(
    private val accountRepository: AccountRepository,
): Transactional {

    suspend fun getAccounts(userId: Long): List<AccountDto> {
        return accountRepository.findAll(userId).map { it.toDto() }
    }
    
    suspend fun getAccount(userId: Long, accountId: Long): AccountDto {
        return getAccounts(userId).firstOrNull { it.id == accountId } ?: throw NoDataFoundException()
    }

    suspend fun createAccount(userId: Long, dto: AccountCreateDto): AccountDto {
        accountRepository.findOneByTitle(userId, dto.title)?.let { throw DuplicateDataException() }

        return accountRepository.create(userId, dto.toModel(userId)).toDto()
    }

    suspend fun updateAccount(userId: Long, accountId: Long, dto: AccountUpdateDto): AccountDto {
        accountRepository.findOneByTitle(userId, dto.title)?.takeIf { it.id != accountId }?.also { throw DuplicateDataException() }

        return accountRepository.update(userId, accountId, dto.toModel())?.toDto() ?: throw NoDataFoundException()
    }

    suspend fun deleteAccounts(userId: Long, dto: BulkDeleteDto) {
        accountRepository.delete(userId, *dto.ids.toLongArray())
    }

    suspend fun deleteAccount(userId: Long, accountId: Long) {
        accountRepository.delete(userId, accountId)
    }
}