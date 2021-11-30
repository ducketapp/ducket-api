package io.ducket.api.domain.repository

import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.follow.Follow
import io.ducket.api.domain.model.follow.FollowEntity
import io.ducket.api.domain.model.follow.FollowsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class FollowRepository {

    fun findOneByFollowerUser(userId: String, followId: String) : Follow? = transaction {
        FollowEntity.find {
            FollowsTable.id.eq(followId).and(FollowsTable.followerUserId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun createRequest(userId: String, userToFollowId: String): Follow = transaction {
        FollowEntity.new {
            followedUser = UserEntity[userToFollowId]
            followerUser = UserEntity[userId]
            createdAt = Instant.now()
        }.toModel()
    }

    fun approveRequest(userId: String, followRequestId: String): Follow? = transaction {
        FollowEntity.find {
            FollowsTable.id.eq(followRequestId).and(FollowsTable.followerUserId.eq(userId))
        }.firstOrNull()?.also { found ->
            found.isPending = false
        }?.toModel()
    }

    fun unfollow(followerUserId: String, followId: String): Boolean = transaction {
        FollowsTable.deleteWhere {
            FollowsTable.followerUserId.eq(followerUserId).and(FollowsTable.id.eq(followId))
        } > 0
    }

    fun findFollowingByUser(userId: String): List<Follow> = transaction {
        FollowEntity.find {
            FollowsTable.followerUserId.eq(userId)
        }.map { it.toModel() }
    }

    fun findFollowersByUser(userId: String): List<Follow> = transaction {
        FollowEntity.find {
            FollowsTable.followedUserId.eq(userId)
        }.map { it.toModel() }
    }
}