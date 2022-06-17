package io.ducket.api.domain.repository

import domain.model.user.UserEntity
import io.ducket.api.domain.model.group.Group
import io.ducket.api.domain.model.group.GroupEntity
import io.ducket.api.domain.model.group.GroupMembershipsTable
import io.ducket.api.domain.model.group.GroupsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class GroupRepository {

    fun create(userId: Long, name: String): Group = transaction {
        GroupEntity.new {
            this.name = name
            this.owner = UserEntity[userId]
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }

    fun updateOne(userId: Long, groupId: Long, name: String): Group? = transaction {
        GroupEntity.find {
            GroupsTable.ownerId.eq(userId).and(GroupsTable.id.eq(groupId))
        }.firstOrNull()?.also { found ->
            found.name = name
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun findOneByOwner(userId: Long, groupId: Long): Group? = transaction {
        GroupEntity.find {
            GroupsTable.ownerId.eq(userId).and(GroupsTable.id.eq(groupId))
        }.firstOrNull()?.toModel()
    }

    fun findOneByMember(userEmail: String, groupId: Long): Group? = transaction {
        GroupEntity.wrapRows(
            GroupsTable.select {
                exists(GroupMembershipsTable.select {
                    GroupMembershipsTable.memberEmail.eq(userEmail)
                })
            }.andWhere { GroupsTable.id.eq(groupId) }
        ).firstOrNull()?.toModel()
    }

    fun findAllByOwner(userId: Long): List<Group> = transaction {
        GroupEntity.find { GroupsTable.ownerId.eq(userId) }.toList().map { it.toModel() }
    }

    fun findAllByMember(userEmail: String): List<Group> = transaction {
        GroupEntity.wrapRows(
            GroupsTable.select {
                exists(GroupMembershipsTable.select {
                    GroupMembershipsTable.memberEmail.eq(userEmail)
                })
            }
        ).map { it.toModel() }
    }

    fun delete(userId: Long, groupId: Long): Unit = transaction {
        GroupsTable.deleteWhere {
            GroupsTable.id.eq(groupId).and(GroupsTable.ownerId.eq(userId))
        }
    }
}