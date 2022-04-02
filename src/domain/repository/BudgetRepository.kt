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

class BudgetRepository(
    private val userRepository: UserRepository,
) {

    fun create(userId: Long, dto: BudgetCreateDto): Budget = transaction {
        BudgetEntity.new {
            name = dto.name
            // category = CategoryEntity[dto.categoryId]
            currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(dto.currencyIsoCode) }.first()
            fromDate = dto.fromDate
            toDate = dto.toDate
            limit = dto.thresholdAmount
            user = UserEntity[userId]
            closed = false
            createdAt = Instant.now()
            modifiedAt = Instant.now()
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

    fun deleteAll(userId: Long) = transaction {
        BudgetsTable.deleteWhere {
            BudgetsTable.userId.eq(userId)
        }
    }
}