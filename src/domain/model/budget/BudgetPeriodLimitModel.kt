package io.ducket.api.domain.model.budget

import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object BudgetPeriodLimitsTable : LongIdTable("budget_period_limit") {
    val budgetId = reference("budget_id", UsersTable)
    val default = bool("is_default")
    val limit = decimal("limit", 10, 2)
    val period = varchar("period", 16) // ISO 8601 or SDMX format
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class BudgetPeriodLimitEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BudgetPeriodLimitEntity>(BudgetPeriodLimitsTable)

    var budget by BudgetEntity referencedOn BudgetPeriodLimitsTable.budgetId

    var default by BudgetPeriodLimitsTable.default
    var limit by BudgetPeriodLimitsTable.limit
    var period by BudgetPeriodLimitsTable.period
    var createdAt by BudgetPeriodLimitsTable.createdAt
    var modifiedAt by BudgetPeriodLimitsTable.modifiedAt

    fun toModel() = BudgetPeriodLimit(
        id = id.value,
        default = default,
        limit = limit,
        period = period,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}

data class BudgetPeriodLimit(
    val id: Long,
    val default: Boolean,
    val limit: BigDecimal,
    val period: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
)