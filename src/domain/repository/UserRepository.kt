package io.ducket.api.domain.repository

import domain.model.currency.CurrenciesTable
import io.ducket.api.domain.controller.user.UserCreateDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import domain.model.currency.CurrencyEntity
import domain.model.user.User
import io.ducket.api.BCRYPT_HASH_ROUNDS
import io.ducket.api.domain.model.follow.FollowEntity
import io.ducket.api.domain.model.follow.FollowsTable

import io.ducket.api.getLogger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

class UserRepository {
    private val logger = getLogger()

    fun create(dto: UserCreateDto): User = transaction {
        val currencyId = CurrencyEntity.find { CurrenciesTable.isoCode.eq(dto.currencyIsoCode) }.first().id

        UserEntity.new {
            name = dto.name
            phone = dto.phone
            email = dto.email
            mainCurrency = CurrencyEntity[currencyId]
            passwordHash = BCrypt.hashpw(dto.password, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findUsersFollowingByUser(userId: Long): List<User> = transaction {
        FollowEntity.wrapRows(
            FollowsTable.select {
                FollowsTable.followerUserId.eq(userId)
                    .and(FollowsTable.isPending.eq(false))
            }
        ).toList().map { it.followedUser.toModel() }
    }

    fun findOneByEmail(email: String): User? = transaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    fun findOne(userId: Long): User? = transaction {
        UserEntity.findById(userId)?.toModel()
    }

    fun findAll(): List<User> = transaction {
        UserEntity.all().map { it.toModel() }
    }

    fun updateOne(userId: Long, dto: UserUpdateDto): User? = transaction {
        UserEntity.findById(userId)?.also { found ->
            dto.name?.let {
                found.name = it
                found.modifiedAt = Instant.now()
            }

            dto.password?.let {
                found.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))
                found.modifiedAt = Instant.now()
            }
        }?.toModel()
    }

    fun deleteOne(userId: Long): Boolean = transaction {
        UsersTable.deleteWhere { UsersTable.id.eq(userId) } > 0
    }
}
