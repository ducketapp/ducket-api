package io.budgery.api.domain.model.budget

import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object BudgetCategoriesTable : IntIdTable("budget_category") {
    val budgetId = reference("budget_id", BudgetsTable)
    val categoryId = reference("category_id", CategoriesTable)
}

class BudgetCategoryEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<BudgetCategoryEntity>(BudgetCategoriesTable)

    var budget by BudgetEntity referencedOn BudgetCategoriesTable.budgetId
    var category by CategoryEntity referencedOn BudgetCategoriesTable.categoryId

    fun toModel() = BudgetCategory(id.value, budget.toModel(), category.toModel())
}

data class BudgetCategory(
    val id: Int,
    val budget: Budget,
    val category: Category,
)