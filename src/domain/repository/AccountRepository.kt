package io.ducket.api.domain.repository

import domain.model.account.*
import domain.model.account.AccountsTable
import domain.model.currency.CurrenciesTable
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.account.AccountUpdateDto
import domain.model.currency.CurrencyEntity
import domain.model.user.UserEntity
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AccountRepository(
    private val userRepository: UserRepository,
) {

    fun create(userId: Long, dto: AccountCreateDto): Account = transaction {
        AccountEntity.new {
            name = dto.name
            notes = dto.notes
            user = UserEntity[userId]
            currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(dto.currencyIsoCode) }.first()
            accountType = dto.accountType
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findOne(userId: Long): Account? = transaction {
        AccountEntity.find {
            AccountsTable.userId.eq(userId)
        }.firstOrNull()?.toModel()
    }

    fun findAll(vararg userIds: Long): List<Account> = transaction {
        AccountEntity.find {
            AccountsTable.userId.inList(userIds.asList())
        }.toList().map { it.toModel() }
    }

//    fun findAll(userId: Long): List<Account> = transaction {
//        AccountEntity.find {
//            AccountsTable.userId.eq(userId)
//        }.sortedByDescending { it.createdAt }.toList().map { it.toModel() }
//    }

    fun findOne(userId: Long, accountId: Long): Account? = transaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: Long, name: String): Account? = transaction {
        AccountEntity.find {
            AccountsTable.name.eq(name).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun updateOne(userId: Long, accountId: Long, dto: AccountUpdateDto): Account? = transaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.also { found ->
            dto.name?.let { found.name = it }
            dto.notes?.let { found.notes = it }
            dto.accountType?.let { found.accountType = it }
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun delete(userId: Long, vararg accountIds: Long) = transaction {
        AccountsTable.deleteWhere {
            AccountsTable.id.inList(accountIds.asList()).and(AccountsTable.userId.eq(userId))
        }
    }

    fun deleteAll(userId: Long): Unit = transaction {
        AccountsTable.deleteWhere {
            AccountsTable.userId.eq(userId)
        }
    }
}