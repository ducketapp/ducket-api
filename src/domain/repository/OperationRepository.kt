package io.ducket.api.domain.repository

import domain.model.category.CategoriesTable
import domain.model.category.CategoryEntity
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.ledger.OperationCreateDto
import domain.model.operation.Operation
import domain.model.operation.OperationEntity
import domain.model.operation.OperationsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class OperationRepository {

    fun create(userId: Long, dto: OperationCreateDto): OperationEntity = transaction {
        val category = CategoryEntity.find {
            CategoriesTable.name.eq(dto.category).and(CategoriesTable.group.eq(dto.categoryGroup))
        }.first()

        OperationEntity.new {
            this.user = UserEntity[userId]
            this.category = category
            this.import = null
            this.description = dto.description
            this.subject = dto.subject
            this.notes = dto.notes
            this.latitude = dto.latitude
            this.longitude = dto.longitude
            this.date = dto.date
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }
    }
}