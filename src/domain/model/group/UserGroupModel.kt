package io.ducket.api.domain.model.group

import domain.model.user.UsersTable
import org.jetbrains.exposed.sql.Table

internal object UserGroupsTable : Table("user_group") {
    val userId = reference("user_id", UsersTable.id)
    val groupId = reference("group_id", GroupsTable.id)

    override val primaryKey = PrimaryKey(userId, groupId)
}