package io.ducket.api.domain.service

import io.ducket.api.CurrencyRatesClient
import io.ducket.api.domain.controller.budget.BudgetCreateDto
import io.ducket.api.domain.controller.budget.BudgetDto
import io.ducket.api.domain.controller.budget.BudgetPeriodBoundsDto
import io.ducket.api.domain.controller.budget.BudgetProgressDto
import io.ducket.api.domain.controller.record.RecordDto
import io.ducket.api.domain.controller.transaction.TransactionDto
import io.ducket.api.domain.model.budget.Budget
import io.ducket.api.domain.model.budget.BudgetPeriodType
import io.ducket.api.domain.repository.*
import io.ducket.api.extension.isAfterInclusive
import io.ducket.api.extension.isBeforeInclusive
import io.ducket.api.extension.sumByDecimal
import io.ducket.api.getLogger
import io.ducket.api.plugins.InvalidDataError
import io.ducket.api.plugins.NoEntityFoundError
import org.koin.java.KoinJavaComponent.inject
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val transferRepository: TransferRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val currencyRepository: CurrencyRepository,
) {
    private val logger = getLogger()
    private val currencyRatesClient: CurrencyRatesClient by inject(CurrencyRatesClient::class.java)

    fun createBudget(userId: Long, reqObj: BudgetCreateDto): BudgetDto {
        val currencyId = currencyRepository.findOne(reqObj.currencyIsoCode)?.id
            ?: throw InvalidDataError("Unsupported '${reqObj.currencyIsoCode}' currency code")

        accountRepository.findAll(userId).map { it.id }.takeIf { it.containsAll(reqObj.accountIds) }
            ?: throw InvalidDataError("No such account(s) found")

        categoryRepository.findById(reqObj.categoryId)
            ?: throw InvalidDataError("No such category found")

        budgetRepository.findOneByName(userId, reqObj.name)?.let {
            if (!it.isClosed) throw InvalidDataError("'${reqObj.name}' budget already exists")
        }

        val budget = budgetRepository.create(userId, currencyId, reqObj)

        return getBudgetDetailsAccessibleToUser(userId, budget.id)
    }

    fun getBudgetDetailsAccessibleToUser(userId: Long, budgetId: Long): BudgetDto {
        return getBudgetsAccessibleToUser(userId).firstOrNull { it.id == budgetId }
            ?: throw NoEntityFoundError("No such budget was found")
    }

    fun getBudgetsAccessibleToUser(userId: Long): List<BudgetDto> {
        return budgetRepository.findAllIncludingObserved(userId).map {
            val periodBounds = getBudgetPeriodBounds(it.periodType)

            if (periodBounds != null) {
                val progressDto = calculateBudgetProgress(it)
                val periodDto = BudgetPeriodBoundsDto(it.periodType.name, periodBounds)
                return@map BudgetDto(it, progressDto, periodDto)
            } else {
                return@map BudgetDto(it, BudgetProgressDto())
            }
        }
    }

    fun deleteBudget(userId: Long, budgetId: Long) {
        budgetRepository.delete(userId, budgetId)
    }

    private fun calculateBudgetProgress(budget: Budget): BudgetProgressDto {
        val transactionRecords = transactionRepository.findAll(budget.user.id).map { TransactionDto(it) }
        val transactions = transactionRecords.sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.amount })

        val periodBounds = getBudgetPeriodBounds(budget.periodType) ?: return BudgetProgressDto()

        val transactionsInPeriod = transactions.filter { transaction ->
            transaction.date.isAfterInclusive(periodBounds.first.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && transaction.date.isBeforeInclusive(periodBounds.second.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && budget.accounts.map { it.id }.contains(transaction.account.id)
                    && budget.category.id == transaction.category?.id
        }

        return resolveTransactionsTotalBalance(transactionsInPeriod, budget.currency.isoCode).let { amount ->
            BudgetProgressDto(transactionsInPeriod.size, budget.limit).also {
                if (amount > BigDecimal.ZERO) {
                    it.progressPercentage = amount * BigDecimal(100) / budget.limit
                    it.moneySpent = amount
                }
            }
        }
    }

    private fun resolveTransactionsTotalBalance(records: List<RecordDto>, currencyIsoCode: String): BigDecimal {
        return records.map {
            val recordCurrencyIsoCode = it.account.accountCurrency.isoCode

            if (recordCurrencyIsoCode != currencyIsoCode) {
                try {
                    val rate = currencyRatesClient.getCurrencyRate(recordCurrencyIsoCode, currencyIsoCode)
                    return@map it.amount * rate
                } catch (e: CurrencyRatesClient.CurrencyRateClientException) {
                    logger.error("Cannot convert record amount: $it")
                    return@map BigDecimal.ZERO
                }
            } else {
                return@map it.amount
            }
        }.sumByDecimal { it }
    }

    private fun getBudgetPeriodBounds(period: BudgetPeriodType): Pair<LocalDate, LocalDate>? {
        val now: LocalDate = LocalDate.now()

        return when (period) {
            BudgetPeriodType.WEEKLY -> Pair(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY))
            BudgetPeriodType.MONTHLY -> Pair(now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth()))
            BudgetPeriodType.ANNUAL -> Pair(now.withDayOfYear(1), now.withDayOfYear(now.lengthOfYear()))
        }
    }
}