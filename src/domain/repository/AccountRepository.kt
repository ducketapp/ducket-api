package io.ducket.api.domain.repository

import domain.model.account.*
import domain.model.account.AccountsTable
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.account.AccountUpdateDto
import domain.model.currency.CurrencyEntity
import domain.model.user.UserEntity
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AccountRepository {

    fun create(userId: String, dto: AccountCreateDto): Account = transaction {
        AccountEntity.new {
            name = dto.name
            notes = dto.notes
            user = UserEntity[userId]
            currency = CurrencyEntity[dto.currencyId]
            accountType = dto.accountType
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findAll(userId: String): List<Account> = transaction {
        AccountEntity.find {
            AccountsTable.userId.eq(userId)
        }.sortedByDescending { it.createdAt }.toList().map { it.toModel() }
    }

    fun findOne(userId: String, accountId: String): Account? = transaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: String, name: String): Account? = transaction {
        AccountEntity.find {
            AccountsTable.name.eq(name).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun updateOne(userId: String, accountId: String, dto: AccountUpdateDto): Account? = transaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.also { found ->
            dto.name?.let { found.name = it }
            dto.notes?.let { found.notes = it }
            dto.accountType?.let { found.accountType = it }
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun deleteOne(userId: String, accountId: String): Boolean = transaction {
        AccountsTable.deleteWhere {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        } > 0
    }
}