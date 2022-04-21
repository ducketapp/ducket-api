package io.ducket.api.domain.repository

import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.app.MembershipStatus
import io.ducket.api.domain.controller.group.GroupMembershipCreateDto
import io.ducket.api.domain.model.group.*
import io.ducket.api.domain.model.group.GroupMembershipsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class GroupMembershipRepository {

    fun findOne(memberId: Long, membershipId: Long): GroupMembership? = transaction {
        GroupMembershipEntity.find {
            GroupMembershipsTable.memberId.eq(memberId).and(GroupMembershipsTable.id.eq(membershipId))
        }.firstOrNull()?.toModel()
    }

    fun findAllByGroup(groupId: Long): List<GroupMembership> = transaction {
        GroupMembershipEntity.find {
            GroupMembershipsTable.groupId.eq(groupId)
        }.toList().map { it.toModel() }
    }

    fun findAllByMember(memberId: Long): List<GroupMembership> = transaction {
        GroupMembershipEntity.find {
            GroupMembershipsTable.memberId.eq(memberId)
        }.toList().map { it.toModel() }
    }

    fun findOneByGroupAndMember(groupId: Long, memberId: Long): GroupMembership? = transaction {
        GroupMembershipEntity.find {
            GroupMembershipsTable.groupId.eq(groupId).and(GroupMembershipsTable.memberId.eq(memberId))
        }.firstOrNull()?.toModel()
    }

    fun create(groupId: Long, dto: GroupMembershipCreateDto, status: MembershipStatus): GroupMembership = transaction {
        GroupMembershipEntity.new {
            this.group = GroupEntity[groupId]
            this.member = UserEntity.find { UsersTable.email.eq(dto.memberEmail) }.first()
            this.status = status
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }

    fun update(memberId: Long, groupId: Long, membershipId: Long, status: MembershipStatus): GroupMembership? = transaction {
        GroupMembershipEntity.find {
            GroupMembershipsTable.id.eq(membershipId)
                .and(GroupMembershipsTable.memberId.eq(memberId))
                .and(GroupMembershipsTable.groupId.eq(groupId))
        }.firstOrNull()?.also {
            it.status = status
            it.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun deleteAll(groupId: Long): Unit = transaction {
        GroupMembershipsTable.deleteWhere { GroupMembershipsTable.groupId.eq(groupId) }
    }

    fun delete(groupId: Long, vararg membershipIds: Long): Unit = transaction {
        GroupMembershipsTable.deleteWhere {
            GroupMembershipsTable.id.inList(membershipIds.asList()).and(GroupMembershipsTable.groupId.eq(groupId))
        }
    }
}