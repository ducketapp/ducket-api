package io.ducket.api.domain.model.budget

import domain.model.category.CategoriesTable
import org.jetbrains.exposed.sql.Table

internal object BudgetCategoriesTable : Table("budget_category") {
    val budgetId = reference("budget_id", BudgetsTable)
    val categoryId = reference("category_id", CategoriesTable)

    override val primaryKey = PrimaryKey(budgetId, categoryId)
}