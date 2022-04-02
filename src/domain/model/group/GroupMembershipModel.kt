package io.ducket.api.domain.model.group

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.app.MembershipStatus
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object GroupMembershipsTable : LongIdTable("group_membership") {
    val groupId = reference("group_id", GroupsTable)
    val memberId = reference("member_id", UsersTable)
    val status = enumerationByName("status", 32, MembershipStatus::class)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class GroupMembershipEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupMembershipEntity>(GroupMembershipsTable)

    var group by GroupEntity referencedOn GroupMembershipsTable.groupId
    var member by UserEntity referencedOn GroupMembershipsTable.memberId
    var status by GroupMembershipsTable.status
    var createdAt by GroupMembershipsTable.createdAt
    var modifiedAt by GroupMembershipsTable.modifiedAt

    fun toModel() = GroupMembership(
        id.value,
        group.toModel(),
        member.toModel(),
        status,
        createdAt,
        modifiedAt,
    )
}

data class GroupMembership(
    val id: Long,
    val group: Group,
    val member: User,
    val status: MembershipStatus,
    val createdAt: Instant,
    val modifiedAt: Instant,
)