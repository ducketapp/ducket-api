package io.ducket.api.domain.repository

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.follow.Follow
import io.ducket.api.domain.model.follow.FollowEntity
import io.ducket.api.domain.model.follow.FollowsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class FollowRepository {

    fun findOneByFollowerUser(userId: Long, followId: Long) : Follow? = transaction {
        FollowEntity.find {
            FollowsTable.id.eq(followId).and(FollowsTable.followerUserId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun findOne(followId: Long) : Follow? = transaction {
        FollowEntity.find {
            FollowsTable.id.eq(followId)
        }.firstOrNull()?.toModel()
    }

    fun createRequest(userId: Long, userToFollowId: Long): Follow = transaction {
        FollowEntity.new {
            followedUser = UserEntity[userToFollowId]
            followerUser = UserEntity[userId]
            createdAt = Instant.now()
        }.toModel()
    }

    fun approveFollow(userId: Long, followRequestId: Long): Follow? = transaction {
        FollowEntity.find {
            FollowsTable.id.eq(followRequestId).and(FollowsTable.followerUserId.eq(userId))
        }.firstOrNull()?.also { found ->
            found.isPending = false
        }?.toModel()
    }

    fun deleteFollow(userId: Long, followId: Long): Boolean = transaction {
        FollowsTable.deleteWhere {
            (FollowsTable.followerUserId.eq(userId).or(FollowsTable.followedUserId.eq(userId)))
                .and(FollowsTable.id.eq(followId))
        } > 0
    }

    fun findFollowsByUser(userId: Long): List<Follow> = transaction {
        FollowEntity.find {
            FollowsTable.followerUserId.eq(userId).or(FollowsTable.followedUserId.eq(userId))
        }.map { it.toModel() }
    }

    fun findFollowingByUser(userId: Long): List<Follow> = transaction {
        FollowEntity.find {
            FollowsTable.followerUserId.eq(userId)
        }.map { it.toModel() }
    }

    fun findFollowersByUser(userId: Long): List<Follow> = transaction {
        FollowEntity.find {
            FollowsTable.followedUserId.eq(userId)
        }.map { it.toModel() }
    }
}