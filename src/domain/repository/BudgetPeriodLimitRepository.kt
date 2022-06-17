package io.ducket.api.domain.repository

import io.ducket.api.domain.model.budget.BudgetEntity
import io.ducket.api.domain.model.budget.BudgetPeriodLimit
import io.ducket.api.domain.model.budget.BudgetPeriodLimitEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.Instant

class BudgetPeriodLimitRepository {

    fun create(budgetId: Long, default: Boolean, limit: BigDecimal, period: String): BudgetPeriodLimit = transaction {
        BudgetPeriodLimitEntity.new {
            this.default = default
            this.budget = BudgetEntity[budgetId]
            this.limit = limit
            this.period = period
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }
}