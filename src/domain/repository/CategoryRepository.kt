package io.ducket.api.domain.repository

import io.ducket.api.domain.model.category.CategoriesTable
import io.ducket.api.domain.model.category.Category
import io.ducket.api.domain.model.category.CategoryEntity
import io.ducket.api.app.database.Transactional
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryRepository: Transactional {

    suspend fun findOneByName(category: String): Category? = blockingTransaction {
        CategoryEntity.find {
            CategoriesTable.name.eq(category)
        }.firstOrNull()?.toModel()
    }

    suspend fun findOne(categoryId: Long): Category? = blockingTransaction {
        CategoryEntity.findById(categoryId)?.toModel()
    }

    // TODO suspend
    fun findAll(): List<Category> = transaction {
        CategoryEntity.all().map { it.toModel() }
    }
}