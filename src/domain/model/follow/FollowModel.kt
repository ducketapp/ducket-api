package io.ducket.api.domain.model.follow

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object FollowsTable : LongIdTable("follow") {
    val followerUserId = reference("follower_id", UsersTable)
    val followedUserId = reference("followed_id", UsersTable)
    val isPending = bool("is_pending").default(true)
    val createdAt = timestamp("created_at")
}

class FollowEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<FollowEntity>(FollowsTable)

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
    val id: Long,
    val follower: User,
    val followed: User,
    val isPending: Boolean,
    val createdAt: Instant,
)