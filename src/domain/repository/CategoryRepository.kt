package io.budgery.api.domain.repository

import domain.model.category.Category
import domain.model.category.CategoryEntity
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryRepository {

    fun findById(categoryId: Int): Category? = transaction {
        CategoryEntity.findById(categoryId)?.toModel()
    }

    fun findAll(): List<Category> = transaction {
        CategoryEntity.all().map { it.toModel() }
    }
}