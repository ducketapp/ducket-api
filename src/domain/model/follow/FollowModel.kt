package io.ducket.api.domain.model.follow

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.StringIdTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

internal object FollowsTable : StringIdTable("follow") {
    val followerUserId = reference("follower_id", UsersTable)
    val followedUserId = reference("followed_id", UsersTable)
    val isPending = bool("followed_id").default(true)
    val createdAt = timestamp("created_at")
}

class FollowEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, FollowEntity>(FollowsTable)

    var followerUser by UserEntity referencedOn FollowsTable.followerUserId
    var followedUser by UserEntity referencedOn FollowsTable.followedUserId
    var isPending by FollowsTable.isPending
    var createdAt by FollowsTable.createdAt

    fun toModel() = Follow(
        id.value,
        followerUser.toModel(),
        followedUser.toModel(),
        isPending,
        createdAt,
    )
}

data class Follow(
    val id: String,
    val follower: User,
    val followed: User,
    val isPending: Boolean,
    val createdAt: Instant,
)