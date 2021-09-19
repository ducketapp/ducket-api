package io.budgery.api.domain.model.budget

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object BudgetPeriodTypesTable: IntIdTable("budget_period_type") {
    val period = varchar("period", 45)
}

class BudgetPeriodTypeEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<BudgetPeriodTypeEntity>(BudgetPeriodTypesTable)

    var period by BudgetPeriodTypesTable.period

    fun toModel() = BudgetPeriodType(id.value, period)
}

data class BudgetPeriodType(
    val id: Int,
    val period: String,
)