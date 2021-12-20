package io.ducket.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.currency.CurrencyEntity
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.budget.BudgetCreateDto
import io.ducket.api.domain.model.budget.*
import io.ducket.api.domain.model.budget.BudgetsTable
import io.ducket.api.getLogger
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class BudgetRepository(
    private val userRepository: UserRepository,
) {

    fun create(userId: Long, currencyId: Long, dto: BudgetCreateDto): Budget = transaction {
        val newBudget = BudgetEntity.new {
            name = dto.name
            category = CategoryEntity[dto.categoryId]
            currency = CurrencyEntity[currencyId]
            periodType = dto.budgetPeriod
            limit = dto.limit
            user = UserEntity[userId]
            isClosed = false
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()

        dto.accountIds.forEach { accountId ->
            BudgetAccountsTable.insert {
                it[this.budgetId] = BudgetEntity[newBudget.id].id.value
                it[this.accountId] = AccountEntity[accountId].id.value
            }
        }

        return@transaction newBudget
    }

    fun findOne(userId: Long, budgetId: Long): Budget? = transaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId).and(BudgetsTable.id.eq(budgetId))
        }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: Long, name: String): Budget? = transaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId).and(BudgetsTable.name.eq(name))
        }.firstOrNull()?.toModel()
    }

    fun findAll(userId: Long): List<Budget> = transaction {
        BudgetEntity.find { BudgetsTable.userId.eq(userId) }.map { it.toModel() }
    }

    fun findAllIncludingObserved(userId: Long): List<Budget> = transaction {
        val followedUsers = userRepository.findUsersFollowingByUser(userId)

        BudgetEntity.wrapRows(
            BudgetsTable.select {
                BudgetsTable.userId.eq(userId)
                    .or(BudgetsTable.userId.inList(followedUsers.map { it.id }))
            }
        ).toList().map { it.toModel() }
    }

    fun delete(userId: Long, vararg budgetIds: Long): Unit = transaction {
        budgetIds.forEach { id ->
            BudgetEntity.find {
                BudgetsTable.id.eq(id).and(BudgetsTable.userId.eq(userId))
            }.firstOrNull()?.delete()
        }
    }
}