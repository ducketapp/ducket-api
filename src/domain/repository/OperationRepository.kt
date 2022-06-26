package io.ducket.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.imports.ImportEntity
import domain.model.operation.OperationCreateModel
import domain.model.operation.OperationModel
import domain.model.user.UserEntity
import domain.model.operation.OperationEntity
import domain.model.operation.OperationsTable
import io.ducket.api.app.database.Transactional
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class OperationRepository: Transactional {

    suspend fun findOne(userId: Long, operationId: Long): OperationModel? = blockingTransaction {
        OperationEntity.find {
            OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findAll(userId: Long): List<OperationModel> = blockingTransaction {
        OperationEntity.find { OperationsTable.userId.eq(userId) }.toList().map { it.toModel() }
    }

    suspend fun createOne(data: OperationCreateModel): OperationModel = blockingTransaction {
        OperationEntity.new {
            this.user = UserEntity[data.userId]
            this.category = data.categoryId?.let { CategoryEntity[it] }
            this.import = data.importId?.let { ImportEntity[it] }
            this.transferAccount = data.transferAccountId?.let { AccountEntity[it] }
            this.account = AccountEntity[data.accountId]
            this.type = data.type
            this.clearedFunds = data.clearedFunds
            this.postedFunds = data.postedFunds
            this.date = data.date
            this.description = data.description
            this.subject = data.subject
            this.notes = data.notes
            this.latitude = data.latitude
            this.longitude = data.longitude
        }.toModel()
    }

    suspend fun delete(userId: Long, vararg operationId: Long): Unit = blockingTransaction {
        OperationsTable.deleteWhere {
            OperationsTable.id.inList(operationId.asList()).and(OperationsTable.userId.eq(userId))
        }
    }
}