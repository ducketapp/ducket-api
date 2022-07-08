package io.ducket.api.domain.repository

import domain.model.account.*
import domain.model.account.AccountsTable
import domain.model.currency.CurrenciesTable
import domain.model.currency.CurrencyEntity
import domain.model.user.UserEntity
import io.ducket.api.app.database.Transactional
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class AccountRepository: Transactional {

    suspend fun create(userId: Long, data: AccountCreate): Account = blockingTransaction {
        AccountEntity.new {
            this.name = data.name
            this.notes = data.notes
            this.user = UserEntity[userId]
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currency) }.first()
            this.type = data.type
        }.toModel()
    }

    suspend fun update(userId: Long, accountId: Long, data: AccountUpdate): Account? = blockingTransaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.apply {
            this.name = data.name
            this.notes = data.notes
            this.type = data.type
        }?.toModel()
    }

    suspend fun findOne(userId: Long): Account? = blockingTransaction {
        AccountEntity.find {
            AccountsTable.userId.eq(userId)
        }.firstOrNull()?.toModel()
    }

    // TODO suspend
    fun findAll(userId: Long): List<Account> = transaction {
        AccountEntity.find {
            AccountsTable.userId.eq(userId)
        }.orderBy(AccountsTable.createdAt to SortOrder.DESC).toList().map { it.toModel() }
    }

    suspend fun findOne(userId: Long, accountId: Long): Account? = blockingTransaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findOneByName(userId: Long, name: String): Account? = blockingTransaction {
        AccountEntity.find {
            AccountsTable.name.eq(name).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    suspend fun delete(userId: Long, vararg accountIds: Long): Unit = blockingTransaction {
        AccountsTable.deleteWhere {
            AccountsTable.id.inList(accountIds.asList()).and(AccountsTable.userId.eq(userId))
        }
    }
}