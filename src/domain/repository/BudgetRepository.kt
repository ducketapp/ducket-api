package io.budgery.api.domain.repository

import io.budgery.api.domain.model.budget.Budget
import io.budgery.api.domain.model.budget.BudgetEntity
import io.budgery.api.domain.model.budget.BudgetsTable
import org.jetbrains.exposed.sql.transactions.transaction

class BudgetRepository {

    fun findAll(userId: Int): List<Budget> = transaction {
        BudgetEntity.find { BudgetsTable.userId.eq(userId) }.map { it.toModel() }
    }
}