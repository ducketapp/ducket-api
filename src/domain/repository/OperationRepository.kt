package io.ducket.api.domain.repository

import domain.model.category.CategoriesTable
import domain.model.category.CategoryEntity
import domain.model.operation.Operation
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.ledger.OperationCreateDto
import domain.model.operation.OperationEntity
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class OperationRepository {

    fun createOne(userId: Long, data: OperationCreateDto): Operation = transaction {
        val categoryEntity = CategoryEntity.find {
            CategoriesTable.name.eq(data.category).and(CategoriesTable.group.eq(data.categoryGroup))
        }.first()

        OperationEntity.new {
            this.user = UserEntity[userId]
            this.category = categoryEntity
            this.import = null
            this.description = data.description
            this.subject = data.subject
            this.notes = data.notes
            this.latitude = data.latitude
            this.longitude = data.longitude
            this.date = data.date
        }.toModel()
    }
}