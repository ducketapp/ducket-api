package io.ducket.api.domain.repository

import domain.model.category.CategoryEntity
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
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(dto.currencyIsoCode) }.first()
            this.category = CategoryEntity[dto.categoryId]
            this.user = UserEntity[userId]
            this.title = dto.title
            // this.defaultLimit = dto.defaultLimit
            this.startDate = dto.startDate
            this.endDate = null
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.also { budget ->
            dto.accountIds.forEach { accountId ->
                BudgetAccountsTable.insert {
                    it[this.budgetId] = budget.id
                    it[this.accountId] = accountId
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
            BudgetsTable.userId.eq(userId).and(BudgetsTable.title.eq(name))
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