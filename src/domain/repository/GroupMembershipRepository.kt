package io.ducket.api.domain.repository

import io.ducket.api.domain.controller.group.GroupMemberCreateDto
import io.ducket.api.domain.controller.group.GroupMemberUpdateDto
import io.ducket.api.domain.model.group.*
import io.ducket.api.domain.model.group.GroupMembershipsTable
import io.ducket.api.domain.model.group.GroupsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class GroupMembershipRepository {

    fun create(groupId: Long, dto: GroupMemberCreateDto): GroupMembership = transaction {
        GroupMembershipEntity.new {
            this.group = GroupEntity[groupId]
            this.memberEmail = dto.memberEmail
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }

    fun findOneByGroupOwnerAndGroup(ownerId: Long, groupId: Long, membershipId: Long): GroupMembership? = transaction {
        GroupMembershipEntity.wrapRows(
            GroupMembershipsTable.select {
                GroupMembershipsTable.groupId.eq(groupId)
                    .and(GroupMembershipsTable.id.eq(membershipId))
                    .and {
                        exists(GroupsTable.select {
                            GroupsTable.ownerId.eq(ownerId).and(GroupsTable.id.eq(groupId))
                        })
                    }
            }
        ).firstOrNull()?.toModel()
    }

    fun findOneByEmailAndGroup(memberEmail: String, groupId: Long): GroupMembership? = transaction {
        GroupMembershipEntity.wrapRows(
            GroupMembershipsTable.select {
                GroupMembershipsTable.groupId.eq(groupId)
                    .and(GroupMembershipsTable.memberEmail.eq(memberEmail))
                    .and {
                        exists(GroupsTable.select {
                            GroupsTable.id.eq(groupId)
                        })
                    }
            }
        ).firstOrNull()?.toModel()
    }

    fun findAllByOwnerAndGroup(ownerId: Long, groupId: Long): List<GroupMembership> = transaction {
        GroupMembershipEntity.wrapRows(
            GroupMembershipsTable.select {
                GroupMembershipsTable.groupId.eq(groupId).and {
                    exists(GroupsTable.select {
                        GroupsTable.ownerId.eq(ownerId).and(GroupsTable.id.eq(groupId))
                    })
                }
            }
        ).toList().map { it.toModel() }
    }

    fun delete(ownerId: Long, groupId: Long, membershipId: Long): Unit = transaction {
        GroupMembershipsTable.deleteWhere {
            GroupMembershipsTable.groupId.eq(groupId).and(GroupMembershipsTable.id.eq(membershipId)).and {
                exists(GroupsTable.select {
                    GroupsTable.ownerId.eq(ownerId).and(GroupsTable.id.eq(groupId))
                })
            }
        }
    }
}