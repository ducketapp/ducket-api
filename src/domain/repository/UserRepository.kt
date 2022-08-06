package dev.ducketapp.service.domain.repository

import dev.ducketapp.service.domain.model.account.AccountsTable
import dev.ducketapp.service.domain.model.currency.CurrenciesTable
import dev.ducketapp.service.domain.model.currency.CurrencyEntity
import dev.ducketapp.service.domain.model.imports.ImportRulesTable
import dev.ducketapp.service.domain.model.imports.ImportsTable
import dev.ducketapp.service.domain.model.operation.OperationsTable
import dev.ducketapp.service.domain.model.user.*
import dev.ducketapp.service.domain.model.user.UsersTable
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetsTable
import dev.ducketapp.service.app.database.Transactional
import dev.ducketapp.service.domain.model.budget.BudgetsTable

import org.jetbrains.exposed.sql.*

class UserRepository: Transactional {

    suspend fun createOne(data: UserCreate): User = blockingTransaction {
        UserEntity.new {
            this.name = data.name
            this.email = data.email
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currency) }.first()
            this.passwordHash = data.passwordHash
        }.toModel()
    }

    suspend fun updateOne(userId: Long, data: UserUpdate): User? = blockingTransaction {
        UserEntity.findById(userId)?.apply {
            this.name = data.name
            this.passwordHash = data.passwordHash
        }?.toModel()
    }

    suspend fun findOneByEmail(email: String): User? = blockingTransaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    suspend fun findOne(userId: Long): User? = blockingTransaction {
        UserEntity.findById(userId)?.toModel()
    }

    suspend fun findAll(): List<User> = blockingTransaction {
        UserEntity.all().map { it.toModel() }
    }

    suspend fun deleteData(userId: Long): Unit = blockingTransaction {
        OperationsTable.deleteWhere { OperationsTable.userId.eq(userId) }
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
