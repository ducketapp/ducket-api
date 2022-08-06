package dev.ducketapp.service.domain.repository

import dev.ducketapp.service.domain.model.category.Category
import dev.ducketapp.service.domain.model.category.CategoryEntity
import dev.ducketapp.service.app.database.Transactional

class CategoryRepository: Transactional {

    suspend fun findOne(categoryId: Long): Category? = blockingTransaction {
        CategoryEntity.findById(categoryId)?.toModel()
    }

    suspend fun findAll(): List<Category> = blockingTransaction {
        CategoryEntity.all().map { it.toModel() }
    }
}