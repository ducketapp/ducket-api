package io.ducket.api.domain.repository

import io.ducket.api.domain.model.imports.*
import io.ducket.api.domain.model.imports.ImportsTable
import io.ducket.api.domain.model.user.UserEntity
import io.ducket.api.app.database.Transactional
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere


class ImportRepository: Transactional {

    suspend fun findAll(userId: Long): List<Import> = blockingTransaction {
        ImportEntity.find { ImportsTable.userId.eq(userId) }.map { it.toModel() }
    }

    suspend fun findOne(userId: Long, importId: Long): Import? = blockingTransaction {
        ImportEntity.find {
            ImportsTable.userId.eq(userId).and(ImportsTable.id.eq(importId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findOneByTitle(userId: Long, title: String): Import? = blockingTransaction {
        ImportEntity.find {
            ImportsTable.userId.eq(userId).and(ImportsTable.title.eq(title))
        }.firstOrNull()?.toModel()
    }

    suspend fun create(data: ImportCreate): Import = blockingTransaction {
        ImportEntity.new {
            this.title = data.title
            this.user = UserEntity[data.userId]
        }.toModel()
    }

    suspend fun update(userId: Long, importId: Long, data: ImportUpdate): Import? = blockingTransaction {
        ImportEntity.find {
            ImportsTable.id.eq(importId).and(ImportsTable.userId.eq(userId))
        }.firstOrNull()?.apply {
            this.title = data.title
        }?.toModel()
    }

    suspend fun delete(userId: Long, vararg importIds: Long): Unit = blockingTransaction {
        ImportsTable.deleteWhere {
            ImportsTable.id.inList(importIds.asList()).and(ImportsTable.userId.eq(userId))
        }
    }
}