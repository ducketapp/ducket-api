package io.ducket.api.domain.model.group

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object UserGroupRequestsTable : LongIdTable("user_group_request") {
    val userId = reference("user_id", UsersTable)
    val groupId = reference("group_id", GroupsTable)
    val message = varchar("message", 128).nullable()
    val isAccepted = bool("is_accepted")
    val createdAt = timestamp("created_at")
}

class UserGroupRequestEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserGroupRequestEntity>(UserGroupRequestsTable)

    var userId by UserEntity referencedOn UserGroupRequestsTable.userId
    var groupId by GroupEntity referencedOn UserGroupRequestsTable.groupId
    var message by UserGroupRequestsTable.message
    var isAccepted by UserGroupRequestsTable.isAccepted
    var createdAt by UserGroupRequestsTable.createdAt

    fun toModel() = UserGroupRequest(
        id.value,
        userId.toModel(),
        groupId.toModel(),
        message,
        isAccepted,
        createdAt,
    )
}

data class UserGroupRequest(
    val id: Long,
    val user: User,
    val group: Group,
    val message: String?,
    val isAccepted: Boolean,
    val createdAt: Instant,
)