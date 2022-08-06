package dev.ducketapp.service.domain.repository

import dev.ducketapp.service.domain.model.account.AccountEntity
import dev.ducketapp.service.domain.model.category.CategoryEntity
import dev.ducketapp.service.domain.model.imports.ImportEntity
import dev.ducketapp.service.domain.model.operation.*
import dev.ducketapp.service.domain.model.operation.OperationsTable
import dev.ducketapp.service.domain.model.user.UserEntity
import dev.ducketapp.service.app.database.Transactional
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere

class OperationRepository: Transactional {

    suspend fun createBatch(dataList: List<OperationCreate>) = blockingTransaction {
        OperationsTable.batchInsert(data = dataList, ignore = true) { data ->
            this[OperationsTable.extId] = data.extId
            this[OperationsTable.userId] = data.userId
            this[OperationsTable.categoryId] = data.categoryId
            this[OperationsTable.importId] = data.importId
            this[OperationsTable.transferAccountId] = data.transferAccountId
            this[OperationsTable.accountId] = data.accountId
            this[OperationsTable.type] = data.type
            this[OperationsTable.clearedAmount] = data.clearedAmount
            this[OperationsTable.postedAmount] = data.postedAmount
            this[OperationsTable.date] = data.date
            this[OperationsTable.description] = data.description
            this[OperationsTable.subject] = data.subject
            this[OperationsTable.notes] = data.notes
            this[OperationsTable.latitude] = data.latitude
            this[OperationsTable.longitude] = data.longitude
        }
    }

    suspend fun createOne(data: OperationCreate): Operation = blockingTransaction {
        OperationEntity.new {
            this.extId = data.extId
            this.user = UserEntity[data.userId]
            this.category = data.categoryId?.let { CategoryEntity[it] }
            this.import = data.importId?.let { ImportEntity[it] }
            this.transferAccount = data.transferAccountId?.let { AccountEntity[it] }
            this.account = AccountEntity[data.accountId]
            this.type = data.type
            this.clearedAmount = data.clearedAmount
            this.postedAmount = data.postedAmount
            this.date = data.date
            this.description = data.description
            this.subject = data.subject
            this.notes = data.notes
            this.latitude = data.latitude
            this.longitude = data.longitude
        }.toModel()
    }

    suspend fun updateOne(userId: Long, operationId: Long, data: OperationUpdate): Operation? = blockingTransaction {
        OperationEntity.find {
            OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId))
        }.firstOrNull()?.apply {
            this.category = data.categoryId?.let { CategoryEntity[it] }
            this.transferAccount = data.transferAccountId?.let { AccountEntity[it] }
            this.account = AccountEntity[data.accountId]
            this.type = data.type
            this.clearedAmount = data.clearedAmount
            this.postedAmount = data.postedAmount
            this.date = data.date
            this.description = data.description
            this.subject = data.subject
            this.notes = data.notes
            this.latitude = data.latitude
            this.longitude = data.longitude
        }?.toModel()
    }

    suspend fun findOne(userId: Long, operationId: Long): Operation? = blockingTransaction {
        OperationEntity.find {
            OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findAll(userId: Long): List<Operation> = blockingTransaction {
        OperationEntity.find {
            OperationsTable.userId.eq(userId)
        }.orderBy(
            OperationsTable.date to SortOrder.DESC,
            OperationsTable.id to SortOrder.DESC,
        ).toList().map { it.toModel() }
    }

    suspend fun delete(userId: Long, vararg operationId: Long): Unit = blockingTransaction {
        OperationsTable.deleteWhere {
            OperationsTable.id.inList(operationId.asList()).and(OperationsTable.userId.eq(userId))
        }
    }
}