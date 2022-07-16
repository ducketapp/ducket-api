package dev.ducket.api.domain.repository

import dev.ducket.api.domain.model.category.Category
import dev.ducket.api.domain.model.category.CategoryEntity
import dev.ducket.api.app.database.Transactional

class CategoryRepository: Transactional {

    suspend fun findOne(categoryId: Long): Category? = blockingTransaction {
        CategoryEntity.findById(categoryId)?.toModel()
    }

    suspend fun findAll(): List<Category> = blockingTransaction {
        CategoryEntity.all().map { it.toModel() }
    }
}