package io.ducket.api.domain.model.group

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object GroupMembershipsTable : LongIdTable("group_membership") {
    val groupId = reference("group_id", GroupsTable)
    val memberEmail = varchar("member_email", 64)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class GroupMembershipEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupMembershipEntity>(GroupMembershipsTable)

    var group by GroupEntity referencedOn GroupMembershipsTable.groupId

    var memberEmail by GroupMembershipsTable.memberEmail
    var createdAt by GroupMembershipsTable.createdAt
    var modifiedAt by GroupMembershipsTable.modifiedAt

    val accountsPermissions by GroupMemberAccountPermissionEntity referrersOn GroupMemberAccountPermissionsTable.membershipId

    fun toModel() = GroupMembership(
        id.value,
        memberEmail,
        accountsPermissions.map { it.toModel() },
        createdAt,
        modifiedAt,
    )
}

data class GroupMembership(
    val id: Long,
    val memberEmail: String,
    val accountsPermissions: List<GroupMemberAccountPermission>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)