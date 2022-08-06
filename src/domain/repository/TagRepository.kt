package dev.ducketapp.service.domain.repository

import dev.ducketapp.service.domain.model.user.UserEntity
import dev.ducketapp.service.app.database.Transactional
import dev.ducketapp.service.domain.model.tag.*
import dev.ducketapp.service.domain.model.tag.TagsTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere


class TagRepository: Transactional {

    suspend fun create(data: TagCreate): Tag = blockingTransaction {
        TagEntity.new {
            this.user = UserEntity[data.userId]
            this.title = data.title
        }.toModel()
    }

    suspend fun update(userId: Long, tagId: Long, data: TagUpdate): Tag? = blockingTransaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.id.eq(tagId))
        }.firstOrNull()?.apply {
            this.title = data.title
        }?.toModel()
    }

    suspend fun findAll(userId: Long): List<Tag> = blockingTransaction {
        TagEntity.find {
            TagsTable.userId.eq(userId)
        }.orderBy(TagsTable.createdAt to SortOrder.DESC).toList().map { it.toModel() }
    }

    suspend fun findOne(userId: Long, tagId: Long): Tag? = blockingTransaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.id.eq(tagId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findOneByTitle(userId: Long, title: String): Tag? = blockingTransaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.title.eq(title))
        }.firstOrNull()?.toModel()
    }

    suspend fun delete(userId: Long, vararg tagIds: Long): Unit = blockingTransaction {
        TagsTable.deleteWhere {
            TagsTable.userId.eq(userId).and(TagsTable.id.inList(tagIds.toList()))
        }
    }
}