package io.budgery.api.domain.repository

import domain.model.account.*
import domain.model.account.AccountTypesTable
import domain.model.account.AccountsTable
import io.budgery.api.domain.controller.account.AccountCreateDto
import io.budgery.api.domain.controller.account.AccountUpdateDto
import domain.model.currency.CurrencyEntity
import domain.model.user.UserEntity
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime

class AccountRepository {

    fun create(userId: Int, accountDto: AccountCreateDto): Account = transaction {
        AccountEntity.new {
            name = accountDto.name
            notes = accountDto.notes
            user = UserEntity[userId]
            currency = CurrencyEntity[accountDto.currencyId]
            accountType = AccountTypeEntity[accountDto.accountTypeId]
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findAll(userId: Int): List<Account> = transaction {
        AccountEntity.find { AccountsTable.userId.eq(userId) }.toList().map { it.toModel() }
    }

    fun findOne(userId: Int, accountId: Int) : Account? = transaction {
        AccountEntity.find { AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId)) }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: Int, name: String) : Account? = transaction {
        AccountEntity.find { AccountsTable.name.eq(name).and(AccountsTable.userId.eq(userId)) }.firstOrNull()?.toModel()
    }

    fun updateOne(userId: Int, accountId: Int, accountDto: AccountUpdateDto) : Account? = transaction {
        AccountEntity.find { AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId)) }.firstOrNull()?.also { found ->
            accountDto.name?.let { found.name = it }
            accountDto.notes?.let { found.notes = it }
            accountDto.accountTypeId?.let { found.accountType = AccountTypeEntity[it] }
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun deleteOne(userId: Int, accountId: Int) : Boolean = transaction {
        AccountEntity.find { AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId)) }.firstOrNull()?.let {
            it.delete()
            findOne(userId, accountId) == null
        } ?: false
    }

    fun findTypeByName(name: String): AccountType? = transaction {
        AccountTypeEntity.find { AccountTypesTable.name.eq(name) }.firstOrNull()?.toModel()
    }
}