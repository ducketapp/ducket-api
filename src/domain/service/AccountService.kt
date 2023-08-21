package org.expenny.service.domain.service

import org.expenny.service.app.database.Transactional
import org.expenny.service.domain.controller.BulkDeleteDto
import org.expenny.service.domain.controller.account.dto.AccountCreateDto
import org.expenny.service.domain.controller.account.dto.AccountDto
import org.expenny.service.domain.controller.account.dto.AccountUpdateDto
import org.expenny.service.domain.mapper.AccountMapper.toDto
import org.expenny.service.domain.mapper.AccountMapper.toModel
import org.expenny.service.domain.repository.*
import org.expenny.service.plugins.DuplicateDataException
import org.expenny.service.plugins.NoDataFoundException

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
        accountRepository.findOneByTitle(userId, dto.name)?.let { throw DuplicateDataException() }

        return accountRepository.create(userId, dto.toModel(userId)).toDto()
    }

    suspend fun updateAccount(userId: Long, accountId: Long, dto: AccountUpdateDto): AccountDto {
        accountRepository.findOneByTitle(userId, dto.name)?.takeIf { it.id != accountId }?.also { throw DuplicateDataException() }

        return accountRepository.update(userId, accountId, dto.toModel())?.toDto() ?: throw NoDataFoundException()
    }

    suspend fun deleteAccounts(userId: Long, dto: BulkDeleteDto) {
        accountRepository.delete(userId, *dto.ids.toLongArray())
    }

    suspend fun deleteAccount(userId: Long, accountId: Long) {
        accountRepository.delete(userId, accountId)
    }
}