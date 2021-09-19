package io.budgery.api.domain.service

import io.budgery.api.ExchangeRateClient
import io.budgery.api.domain.controller.budget.BudgetCreateDto
import io.budgery.api.domain.controller.budget.BudgetDto
import io.budgery.api.domain.controller.budget.BudgetPeriodDto
import io.budgery.api.domain.controller.budget.BudgetProgressDto
import io.budgery.api.domain.controller.record.RecordDto
import io.budgery.api.domain.model.budget.Budget
import io.budgery.api.domain.repository.*
import io.budgery.api.extension.isAfterInclusive
import io.budgery.api.extension.isBeforeInclusive
import io.budgery.api.extension.sumByDecimal
import io.budgery.api.getLogger
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
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

    fun createBudget(userId: Int, reqObj: BudgetCreateDto): BudgetDto {
        val periodTypeId = budgetRepository.findPeriodType(reqObj.budgetPeriod)?.id
            ?: throw IllegalArgumentException("Unsupported '${reqObj.budgetPeriod}' period type")

        val currencyId = currencyRepository.findOne(reqObj.currencyIsoCode)?.id
            ?: throw IllegalArgumentException("Unsupported '${reqObj.currencyIsoCode}' currency code")

        accountRepository.findAll(userId).map { it.id }.takeIf { it.containsAll(reqObj.accountIds) }
            ?: throw IllegalArgumentException("No such account(s) found")

        categoryRepository.findById(reqObj.categoryId)
            ?: throw IllegalArgumentException("No such category found")

        budgetRepository.findOneByName(userId, reqObj.name)?.let {
            if (!it.isClosed) throw IllegalArgumentException("'${reqObj.name}' budget already opened")
        }

        val budget = budgetRepository.create(userId, periodTypeId, currencyId, reqObj)

        return getBudget(userId, budget.id)
    }

    fun getBudget(userId: Int, budgetId: Int): BudgetDto {
        return getBudgets(userId).firstOrNull { it.id == budgetId }
            ?: throw NoSuchElementException("No such budget was found")
    }

    fun getBudgets(userId: Int): List<BudgetDto> {
        return budgetRepository.findAll(userId).map {
            val periodBounds = getBudgetPeriodBounds(it.periodType.period)

            if (periodBounds != null) {
                val progressDto = calculateBudgetProgress(it)
                val periodDto = BudgetPeriodDto(it.periodType.period, periodBounds)
                return@map BudgetDto(it, progressDto, periodDto)
            } else {
                return@map BudgetDto(it, BudgetProgressDto())
            }
        }
    }

    private fun calculateBudgetProgress(budget: Budget): BudgetProgressDto {
        val transactionRecords = transactionRepository.findAll(budget.user.id).map { RecordDto(it) }
        val transferRecords = transferRepository.findAll(budget.user.id).map { RecordDto(it) }
        val records = transactionRecords.plus(transferRecords).sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.amount })

        val periodBounds = getBudgetPeriodBounds(budget.periodType.period)
            ?: return BudgetProgressDto(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        val recordsInPeriod = records.filter { r ->
            r.date.isAfterInclusive(periodBounds.first.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && r.date.isBeforeInclusive(periodBounds.second.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && budget.accounts.map { it.id }.contains(r.account.id)
                    && budget.category.id == r.category.id
        }

        val amount = getRecordsTotalBalance(recordsInPeriod, budget.currency.isoCode)
        var progress = BigDecimal.ZERO
        var spent = BigDecimal.ZERO

        if (amount < BigDecimal.ZERO) {
            progress = amount * BigDecimal(100) / budget.limit
            spent = amount
        }

        return BudgetProgressDto(recordsInPeriod.size, budget.limit, progress, spent)
    }

    private fun getRecordsTotalBalance(records: List<RecordDto>, currencyIsoCode: String): BigDecimal {
        val exchangeRateClient = ExchangeRateClient()

        return records.map {
            val recordCurrencyIsoCode = it.account.accountCurrency.isoCode

            if (recordCurrencyIsoCode != currencyIsoCode) {
                try {
                    val rate = exchangeRateClient.getRate(recordCurrencyIsoCode, currencyIsoCode)
                    return@map it.amount * rate
                } catch (e: ExchangeRateClient.ExchangeRateClientException) {
                    logger.error("Cannot convert record amount: $it")
                    return@map BigDecimal.ZERO
                }
            } else {
                return@map it.amount
            }
        }.sumByDecimal { it }
    }

    private fun getBudgetPeriodBounds(period: String): Pair<LocalDate, LocalDate>? {
        val now: LocalDate = LocalDate.now()

        return when (period) {
            "WEEKLY" -> Pair(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY))
            "MONTHLY" -> Pair(now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth()))
            "ANNUAL" -> Pair(now.withDayOfYear(1), now.withDayOfYear(now.lengthOfYear()))
            else -> {
                logger.error("Invalid budget period: $period")
                null
            }
        }
    }
}