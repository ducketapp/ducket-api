package io.ducket.api.domain.repository

import domain.model.account.*
import domain.model.account.AccountsTable
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.account.AccountUpdateDto
import domain.model.currency.CurrencyEntity
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import domain.model.user.UserEntity
import io.ducket.api.domain.model.follow.FollowEntity
import io.ducket.api.domain.model.follow.FollowsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AccountRepository(
    private val userRepository: UserRepository,
) {

    fun create(userId: Long, dto: AccountCreateDto): Account = transaction {
        AccountEntity.new {
            name = dto.name
            notes = dto.notes
            user = UserEntity[userId]
            currency = CurrencyEntity[dto.currencyId]
            accountType = dto.accountType
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findAllIncludingObserved(userId: Long): List<Account> = transaction {
        val followedUsers = userRepository.findUsersFollowingByUser(userId)

        AccountEntity.wrapRows(
            AccountsTable.select {
                AccountsTable.userId.eq(userId)
                    .or(AccountsTable.userId.inList(followedUsers.map { it.id }))
            }
        ).toList().map { it.toModel() }
    }

    fun findAll(userId: Long): List<Account> = transaction {
        AccountEntity.find {
            AccountsTable.userId.eq(userId)
        }.sortedByDescending { it.createdAt }.toList().map { it.toModel() }
    }

    fun findOne(userId: Long, accountId: Long): Account? = transaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: Long, name: String): Account? = transaction {
        AccountEntity.find {
            AccountsTable.name.eq(name).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun updateOne(userId: Long, accountId: Long, dto: AccountUpdateDto): Account? = transaction {
        AccountEntity.find {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        }.firstOrNull()?.also { found ->
            dto.name?.let { found.name = it }
            dto.notes?.let { found.notes = it }
            dto.accountType?.let { found.accountType = it }
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun deleteOne(userId: Long, accountId: Long): Boolean = transaction {
        AccountsTable.deleteWhere {
            AccountsTable.id.eq(accountId).and(AccountsTable.userId.eq(userId))
        } > 0
    }
}