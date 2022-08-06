package dev.ducketapp.service.domain.service

import dev.ducketapp.service.app.database.Transactional
import dev.ducketapp.service.domain.controller.BulkDeleteDto
import dev.ducketapp.service.domain.controller.account.dto.AccountCreateDto
import dev.ducketapp.service.domain.controller.account.dto.AccountDto
import dev.ducketapp.service.domain.controller.account.dto.AccountUpdateDto
import dev.ducketapp.service.domain.mapper.AccountMapper.toDto
import dev.ducketapp.service.domain.mapper.AccountMapper.toModel
import dev.ducketapp.service.domain.repository.*
import dev.ducketapp.service.plugins.DuplicateDataException
import dev.ducketapp.service.plugins.NoDataFoundException

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