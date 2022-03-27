package io.ducket.api.domain.model.group

import domain.model.user.UsersTable
import org.jetbrains.exposed.sql.Table

internal object UserGroupMembershipsTable : Table("user_group_membership") {
    val userId = reference("user_id", UsersTable.id)
    val membershipId = reference("membership_id", GroupMembershipsTable.id)

    override val primaryKey = PrimaryKey(userId, membershipId)
}