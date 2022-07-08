package io.ducket.api.domain.repository

import domain.model.account.AccountsTable
import domain.model.currency.CurrenciesTable
import domain.model.currency.CurrencyEntity
import domain.model.imports.ImportRulesTable
import domain.model.imports.ImportsTable
import domain.model.operation.OperationsTable
import domain.model.user.*
import domain.model.user.UsersTable
import domain.model.periodic_budget.PeriodicBudgetsTable
import io.ducket.api.domain.model.group.GroupsTable
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.model.budget.BudgetsTable

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository: Transactional {

    suspend fun createOne(data: UserCreate): User = blockingTransaction {
        UserEntity.new {
            this.name = data.name
            this.phone = data.phone
            this.email = data.email
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currency) }.first()
            this.passwordHash = data.passwordHash
        }.toModel()
    }

    suspend fun updateOne(userId: Long, data: UserUpdate): User? = blockingTransaction {
        UserEntity.findById(userId)?.apply {
            this.name = data.name
            this.phone = data.phone
            this.passwordHash = data.passwordHash
        }?.toModel()
    }

    suspend fun findOneByEmail(email: String): User? = blockingTransaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    // TODO suspend
    fun findOne(userId: Long): User? = transaction {
        UserEntity.findById(userId)?.toModel()
    }

    suspend fun findAll(): List<User> = blockingTransaction {
        UserEntity.all().map { it.toModel() }
    }

    suspend fun deleteData(userId: Long): Unit = blockingTransaction {
        OperationsTable.deleteWhere { OperationsTable.userId.eq(userId) }
        GroupsTable.deleteWhere { GroupsTable.ownerId.eq(userId) }
        BudgetsTable.deleteWhere { BudgetsTable.userId.eq(userId) }
        PeriodicBudgetsTable.deleteWhere { PeriodicBudgetsTable.userId.eq(userId) }
        ImportsTable.deleteWhere { ImportsTable.userId.eq(userId) }
        ImportRulesTable.deleteWhere { ImportRulesTable.userId.eq(userId) }
        AccountsTable.deleteWhere { AccountsTable.userId.eq(userId) }
    }

    suspend fun deleteOne(userId: Long): Unit = blockingTransaction {
        UserEntity.findById(userId)?.delete()
    }
}
