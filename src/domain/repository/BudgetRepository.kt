package io.ducket.api.domain.repository

import domain.model.currency.CurrenciesTable
import domain.model.currency.CurrencyEntity
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.budget.BudgetCreateDto
import io.ducket.api.domain.model.budget.*
import io.ducket.api.domain.model.budget.BudgetsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class BudgetRepository {

    fun create(userId: Long, dto: BudgetCreateDto): Budget = transaction {
        BudgetEntity.new {
            this.name = dto.name
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(dto.currencyIsoCode) }.first()
            this.fromDate = dto.fromDate
            this.toDate = dto.toDate
            this.limit = dto.threshold
            this.user = UserEntity[userId]
            this.closed = false
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.also { newBudget ->
            dto.accountIds.forEach { accountId ->
                BudgetAccountsTable.insert {
                    it[this.budgetId] = newBudget.id
                    it[this.accountId] = accountId
                }
            }

            dto.categoryIds.forEach { categoryId ->
                BudgetCategoriesTable.insert {
                    it[this.budgetId] = newBudget.id
                    it[this.categoryId] = categoryId
                }
            }
        }.toModel()
    }

    fun findOne(userId: Long, budgetId: Long): Budget? = transaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId).and(BudgetsTable.id.eq(budgetId))
        }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: Long, name: String): Budget? = transaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId).and(BudgetsTable.name.eq(name))
        }.firstOrNull()?.toModel()
    }

    fun findAll(vararg userIds: Long): List<Budget> = transaction {
        BudgetEntity.find {
            BudgetsTable.userId.inList(userIds.asList())
        }.toList().map { it.toModel() }
    }

    fun delete(userId: Long, vararg budgetIds: Long) = transaction {
        BudgetsTable.deleteWhere {
            BudgetsTable.id.inList(budgetIds.asList()).and(BudgetsTable.userId.eq(userId))
        }
    }
}