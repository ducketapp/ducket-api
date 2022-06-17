package io.ducket.api.domain.repository

import domain.model.user.UserEntity
import io.ducket.api.domain.controller.tag.TagCreateDto
import io.ducket.api.domain.controller.tag.TagUpdateDto
import io.ducket.api.domain.model.tag.Tag
import io.ducket.api.domain.model.tag.TagEntity
import io.ducket.api.domain.model.tag.TagsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class TagRepository {

    fun findAll(userId: Long): List<Tag> = transaction {
        TagEntity.find { TagsTable.userId.eq(userId) }.toList().map { it.toModel() }
    }

    fun findOne(userId: Long, tagId: Long): Tag? = transaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.id.eq(tagId))
        }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: Long, tagName: String): Tag? = transaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.name.eq(tagName))
        }.firstOrNull()?.toModel()
    }

    fun createOne(userId: Long, dto: TagCreateDto): Tag = transaction {
        TagEntity.new {
            this.user = UserEntity[userId]
            this.name = dto.name
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }

    fun updateOne(userId: Long, tagId: Long, dto: TagUpdateDto): Tag = transaction {
        TagEntity.find {
            TagsTable.userId.eq(userId).and(TagsTable.id.eq(tagId))
        }.firstOrNull()?.also { found ->
            found.name = dto.name
            found.modifiedAt = Instant.now()
        }!!.toModel()
    }

    fun delete(userId: Long, vararg tagIds: Long): Unit = transaction {
        TagsTable.deleteWhere {
            TagsTable.userId.eq(userId).and(TagsTable.id.inList(tagIds.toList()))
        }
    }
}