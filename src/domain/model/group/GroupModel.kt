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

internal object GroupsTable : LongIdTable("group") {
    val name = varchar("name", 32)
    val ownerId = reference("owner_id", UsersTable)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class GroupEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupEntity>(GroupsTable)

    var name by GroupsTable.name
    var owner by UserEntity referencedOn GroupsTable.ownerId
    var createdAt by GroupsTable.createdAt
    var modifiedAt by GroupsTable.modifiedAt

    val memberships by GroupMembershipEntity referrersOn GroupMembershipsTable.groupId

    fun toModel() = Group(
        id.value,
        name,
        owner.toModel(),
        memberships.map { it.toModel() },
        createdAt,
        modifiedAt,
    )
}

data class Group(
    val id: Long,
    val name: String,
    val owner: User,
    val memberships: List<GroupMembership>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)