package io.budgery.api.domain.repository

import io.budgery.api.domain.controller.user.UserSignUpDto
import io.budgery.api.domain.controller.user.UserUpdateDto
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import domain.model.currency.CurrencyEntity
import domain.model.user.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.time.LocalDateTime

class UserRepository {

    fun create(userDto: UserSignUpDto): User = transaction {
         UserEntity.new {
            name = userDto.name
            email = userDto.email
            mainCurrency = CurrencyEntity[userDto.currencyId]
            passwordHash = BCrypt.hashpw(userDto.password, BCrypt.gensalt(10))
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findByEmail(email: String): User? = transaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    fun findById(userId: Int): User? = transaction {
        UserEntity.findById(userId)?.toModel()
    }

    fun updateById(userId: Int, userDto: UserUpdateDto): User? = transaction {
        UserEntity.findById(userId)?.also { found ->
            userDto.name?.let { found.name = it }
            userDto.password?.let { found.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt(10)) }
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun deleteById(userId: Int): Boolean = transaction {
        UserEntity.findById(userId)?.let {
            it.delete()
            findById(userId) == null
        } ?: false
    }
}
