package io.ducket.api.domain.repository

import domain.model.user.UserEntity
import io.ducket.api.app.MembershipStatus
import io.ducket.api.domain.controller.group.GroupCreateDto
import io.ducket.api.domain.controller.group.GroupUpdateDto
import io.ducket.api.domain.model.group.*
import io.ducket.api.domain.model.group.GroupsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class GroupRepository {

    fun create(userId: Long, dto: GroupCreateDto): Group = transaction {
        GroupEntity.new {
            name = dto.name
            creator = UserEntity[userId]
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun updateOne(creatorId: Long, groupId: Long, dto: GroupUpdateDto): Group? = transaction {
        GroupEntity.find {
            GroupsTable.id.eq(groupId).and(GroupsTable.creatorId.eq(creatorId))
        }.firstOrNull()?.also { found ->
            dto.name?.let {
                found.name = dto.name
                found.modifiedAt = Instant.now()
            }
        }?.toModel()
    }

    fun findOne(groupId: Long): Group? = transaction {
        GroupEntity.findById(groupId)?.toModel()
    }

    fun findOneByCreatorAndName(creatorId: Long, name: String): Group? = transaction {
        GroupEntity.find {
            GroupsTable.name.eq(name).and(GroupsTable.creatorId.eq(creatorId))
        }.firstOrNull()?.toModel()
    }

    fun findAllByMember(memberId: Long): List<Group> = transaction {
        GroupMembershipEntity.find {
            GroupMembershipsTable.memberId.eq(memberId)
        }.toList().map { it.group.toModel() }
    }

    fun delete(userId: Long, vararg groupIds: Long) = transaction {
        GroupsTable.deleteWhere {
            GroupsTable.id.inList(groupIds.asList()).and(GroupsTable.creatorId.eq(userId))
        }
    }
}