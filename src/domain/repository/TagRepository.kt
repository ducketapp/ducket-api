package io.ducket.api.domain.repository

import domain.model.user.UserEntity
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.tag.TagCreateDto
import io.ducket.api.domain.controller.tag.TagUpdateDto
import io.ducket.api.domain.model.tag.Tag
import io.ducket.api.domain.model.tag.TagEntity
import io.ducket.api.domain.model.tag.TagsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere


class TagRepository: Transactional {

    suspend fun findAll(userId: Long): List<Tag> = blockingTransaction {
        TagEntity.find { TagsTable.userId.eq(userId) }.toList().map { it.toModel() }
    }

    suspend fun findOne(userId: Long, tagId: Long): Tag? = blockingTransaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.id.eq(tagId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findOneByName(userId: Long, tagName: String): Tag? = blockingTransaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.name.eq(tagName))
        }.firstOrNull()?.toModel()
    }

    suspend fun createOne(userId: Long, data: TagCreateDto): Tag = blockingTransaction {
        TagEntity.new {
            this.user = UserEntity[userId]
            this.name = data.name
        }.toModel()
    }

    suspend fun updateOne(userId: Long, tagId: Long, data: TagUpdateDto): Tag? = blockingTransaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.id.eq(tagId))
        }.firstOrNull()?.also { entity ->
            entity.name = data.name
        }?.toModel()
    }

    suspend fun delete(userId: Long, vararg tagIds: Long): Unit = blockingTransaction {
        TagsTable.deleteWhere {
            TagsTable.userId.eq(userId).and(TagsTable.id.inList(tagIds.toList()))
        }
    }
}