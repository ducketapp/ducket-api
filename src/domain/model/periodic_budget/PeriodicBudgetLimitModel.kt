package dev.ducketapp.service.domain.model.periodic_budget

import dev.ducketapp.service.app.DEFAULT_SCALE
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

internal object PeriodicBudgetLimitsTable : LongIdTable("periodic_budget_limit") {
    val budgetId = reference("budget_id", PeriodicBudgetsTable)
    val default = bool("is_default")
    val limit = decimal("limit", 10, DEFAULT_SCALE)
    val fromDate = date("from_date")
    val toDate = date("to_date")
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class PeriodicBudgetLimitEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PeriodicBudgetLimitEntity>(PeriodicBudgetLimitsTable)

    var budget by PeriodicBudgetEntity referencedOn PeriodicBudgetLimitsTable.budgetId

    var default by PeriodicBudgetLimitsTable.default
    var limit by PeriodicBudgetLimitsTable.limit
    var fromDate by PeriodicBudgetLimitsTable.fromDate
    var toDate by PeriodicBudgetLimitsTable.toDate
    var createdAt by PeriodicBudgetLimitsTable.createdAt
    var modifiedAt by PeriodicBudgetLimitsTable.modifiedAt

    fun toModel() = PeriodicBudgetLimit(
        id = id.value,
        budget = budget.toModel(),
        default = default,
        limit = limit,
        fromDate = fromDate,
        toDate = toDate,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}

data class PeriodicBudgetLimit(
    val id: Long,
    val budget: PeriodicBudget,
    val default: Boolean,
    val limit: BigDecimal,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

data class PeriodicBudgetLimitCreate(
    val budgetId: Long,
    val limit: BigDecimal,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val default: Boolean,
)

data class PeriodicBudgetLimitUpdate(
    val limit: BigDecimal,
    val fromDate: LocalDate,
    val toDate: LocalDate,
)