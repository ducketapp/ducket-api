package io.ducket.api.domain.service

import io.ducket.api.CurrencyRateProvider
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.budget.BudgetCreateDto
import io.ducket.api.domain.controller.budget.BudgetDto
import io.ducket.api.domain.controller.budget.BudgetProgressDto
import io.ducket.api.domain.controller.record.RecordDto
import io.ducket.api.domain.controller.transaction.TransactionDto
import io.ducket.api.domain.model.budget.Budget
import io.ducket.api.domain.model.budget.BudgetAccountsTable
import io.ducket.api.domain.model.budget.BudgetCategoriesTable
import io.ducket.api.domain.model.budget.BudgetEntity
import io.ducket.api.domain.repository.*
import io.ducket.api.extension.isAfterInclusive
import io.ducket.api.extension.isBeforeInclusive
import io.ducket.api.extension.lt
import io.ducket.api.extension.sumByDecimal
import io.ducket.api.getLogger
import io.ducket.api.plugins.DuplicateEntityException
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.java.KoinJavaComponent.inject
import java.math.BigDecimal
import java.time.ZoneId

class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val groupService: GroupService,
) {
    private val logger = getLogger()
    private val currencyRateProvider: CurrencyRateProvider by inject(CurrencyRateProvider::class.java)

    fun createBudget(userId: Long, payload: BudgetCreateDto): BudgetDto {
        if (payload.fromDate.isAfter(payload.toDate)) {
            throw InvalidDataException("Invalid date bounds")
        }

        budgetRepository.findOneByName(userId, payload.name)?.let {
            throw DuplicateEntityException()
        }

        return transaction {
            // create budget
            val newBudget = budgetRepository.create(userId, payload)

            // create budget account mappings
            payload.accountIds.forEach { accountId ->
                BudgetAccountsTable.insert {
                    it[this.budgetId] = newBudget.id
                    it[this.accountId] = accountId
                }
            }

            // create budget categories mappings
            payload.categoryIds.forEach { categoryId ->
                BudgetCategoriesTable.insert {
                    it[this.budgetId] = newBudget.id
                    it[this.categoryId] = categoryId
                }
            }

            return@transaction BudgetDto(
                budget = BudgetEntity[newBudget.id].toModel(),
                progress = calculateBudgetProgress(newBudget),
            )
        }
    }

    fun getBudgetAccessibleToUser(userId: Long, budgetId: Long): BudgetDto {
        return getBudgetsAccessibleToUser(userId).firstOrNull { it.id == budgetId } ?: throw NoEntityFoundException()
    }

    fun getBudgetsAccessibleToUser(userId: Long): List<BudgetDto> {
        val userIds = groupService.getDistinctUsersWithMutualGroupMemberships(userId).map { it.id } + userId

        return budgetRepository.findAll(*userIds.toLongArray()).map {
            return@map BudgetDto(
                budget = it,
                progress = calculateBudgetProgress(it),
            )
        }
    }

    fun deleteBudgets(userId: Long, payload: BulkDeleteDto) {
        budgetRepository.delete(userId, *payload.ids.toLongArray())
    }

    fun deleteBudget(userId: Long, budgetId: Long) {
        budgetRepository.delete(userId, budgetId)
    }

    private fun calculateBudgetProgress(budget: Budget): BudgetProgressDto {
        val transactions = transactionRepository.findAll(budget.user.id)
            .map { TransactionDto(it) }
            .sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.amount })

        val affectedTransactions = transactions.filter { transaction ->
            transaction.date.isAfterInclusive(budget.fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && transaction.date.isBeforeInclusive(budget.toDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && budget.accounts.map { it.id }.contains(transaction.account.id)
                    && budget.categories.map { it.id }.contains(transaction.category.id)
        }

        return resolveTransactionsTotalBalance(affectedTransactions, budget.currency.isoCode).let { amount ->
            BudgetProgressDto().also {
                if (amount.lt(BigDecimal.ZERO)) {
                    it.percentage = amount.abs() * BigDecimal(100) / budget.limit
                    it.amount = amount.abs()
                }
            }
        }
    }

    private fun resolveTransactionsTotalBalance(records: List<RecordDto>, currencyIsoCode: String): BigDecimal {
        return records.map {
            val recordCurrencyIsoCode = it.account.accountCurrency.isoCode

            if (recordCurrencyIsoCode != currencyIsoCode) {
                try {
                    val rate = currencyRateProvider.getCurrencyRate(
                        base = recordCurrencyIsoCode,
                        term = currencyIsoCode
                    )
                    return@map it.amount * rate
                } catch (e: CurrencyRateProvider.CurrencyRateClientException) {
                    logger.error("Cannot convert record amount: $it")
                    return@map BigDecimal.ZERO
                }
            } else {
                return@map it.amount
            }
        }.sumByDecimal { it }
    }
}