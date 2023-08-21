package org.expenny.service.domain.repository

import org.expenny.service.domain.model.category.Category
import org.expenny.service.domain.model.category.CategoryEntity
import org.expenny.service.app.database.Transactional

class CategoryRepository: Transactional {

    suspend fun findOne(categoryId: Long): Category? = blockingTransaction {
        CategoryEntity.findById(categoryId)?.toModel()
    }

    suspend fun findAll(): List<Category> = blockingTransaction {
        CategoryEntity.all().map { it.toModel() }
    }
}