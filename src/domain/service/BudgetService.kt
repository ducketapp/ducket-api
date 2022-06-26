package io.ducket.api.domain.service

import clients.rates.ReferenceRatesClient
import io.ducket.api.app.BudgetPeriodType
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.budget.BudgetCreateDto
import io.ducket.api.domain.controller.budget.BudgetDto
import io.ducket.api.domain.controller.operation.OperationDto
import io.ducket.api.domain.model.budget.Budget
import io.ducket.api.domain.repository.*
import io.ducket.api.getLogger
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException
import io.ducket.api.utils.*
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import org.threeten.extra.LocalDateRange
import org.threeten.extra.YearQuarter
import org.threeten.extra.YearWeek
import java.math.BigDecimal
import java.time.*

class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val budgetPeriodLimitRepository: BudgetPeriodLimitRepository,
    private val groupService: GroupService,
    private val operationService: OperationService,
) {
    private val logger = getLogger()
    private val ratesClient: ReferenceRatesClient by inject(ReferenceRatesClient::class.java)

    fun createBudget(userId: Long, payload: BudgetCreateDto): BudgetDto {
        budgetRepository.findOneByName(userId, payload.title)?.run {
            throw DuplicateDataException()
        }

        val budget = budgetRepository.create(userId, payload)
        val budgetPeriodLimit = budgetPeriodLimitRepository.create(
            budgetId = budget.id,
            default = true,
            limit = payload.defaultLimit,
            period = payload.currentPeriod,
        )
        val budgetPeriodProgress = getBudgetPeriodProgress(budget, budgetPeriodLimit.period)

        return BudgetDto(budget, budgetPeriodLimit, budgetPeriodProgress)
    }

    fun getBudget(userId: Long, budgetId: Long, period: String?): BudgetDto {
        val budget = budgetRepository.findOne(userId, budgetId) ?: throw NoDataFoundException()
        val budgetPeriodLimit = budget.limits
            .filter { it.period == period || it.default }
            .sortedBy { it.default }
            .lastOrNull()!!
        val budgetPeriodProgress = getBudgetPeriodProgress(budget, budgetPeriodLimit.period)

        return BudgetDto(budget, budgetPeriodLimit, budgetPeriodProgress)
    }

//    fun getBudgets(userId: Long): List<BudgetDto> {
//        budgetRepository.findAll(userId).map { budget ->
//
//        }
//    }

    fun deleteBudgets(userId: Long, payload: BulkDeleteDto) {
        budgetRepository.delete(userId, *payload.ids.toLongArray())
    }

    fun deleteBudget(userId: Long, budgetId: Long) {
        budgetRepository.delete(userId, budgetId)
    }

    private fun getPeriodDateRange(period: String, periodType: BudgetPeriodType): LocalDateRange {
        return when (periodType) {
            BudgetPeriodType.DAILY -> {
                LocalDate.parse(period).let { LocalDateRange.ofClosed(it, it) }
            }
            BudgetPeriodType.WEEKLY -> {
                LocalDateRange.ofClosed(
                    YearWeek.parse(period).atDay(DayOfWeek.MONDAY),
                    YearWeek.parse(period).atDay(DayOfWeek.SUNDAY)
                )
            }
            BudgetPeriodType.MONTHLY -> {
                LocalDateRange.ofClosed(
                    YearMonth.parse(period).atDay(1),
                    YearMonth.parse(period).atEndOfMonth()
                )
            }
            BudgetPeriodType.QUARTERLY -> {
                LocalDateRange.ofClosed(
                    YearQuarter.parse(period).atDay(1),
                    YearQuarter.parse(period).atEndOfQuarter()
                )
            }
            BudgetPeriodType.ANNUALLY -> {
                LocalDateRange.ofClosed(
                    LocalDate.of(period.toInt(), Month.JANUARY, 1),
                    LocalDate.of(period.toInt(), Month.DECEMBER, 31)
                )
            }
        }
    }

    private fun getBudgetPeriodProgress(budget: Budget, period: String): BigDecimal {
        val periodRange = getPeriodDateRange(period, budget.periodType)

        if (budget.endDate != null) {
            val budgetRange = LocalDateRange.ofClosed(budget.startDate, budget.endDate)
            if (!budgetRange.encloses(periodRange)) {
                return BigDecimal.ZERO
            }
        } else {
            if (periodRange.start.isBefore(budget.startDate)) {
                return BigDecimal.ZERO
            }
        }

        val ledgerRecords = runBlocking {
            operationService.getOperations(budget.user.id).filter { operation ->
                periodRange.contains(operation.date.toLocalDate())
                        && budget.accounts.map { it.id }.contains(operation.account.id)
                        && budget.category.id == operation.category?.id
            }
        }

        return getRecordsTotalBalance(ledgerRecords, budget.currency.isoCode)
            .takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO
    }

    private fun getRecordsTotalBalance(records: List<OperationDto>, currency: String): BigDecimal {
        return records.map { record ->
            val recordCurrency = record.account.currency.isoCode

            if (recordCurrency != currency) {
                try {
//                    val rate = runBlocking {
//                        ecbClient.getFirstRateStartingFromDate(recordCurrency, currency, record.operation.date.toLocalDate())
//                    }
//                    return@map record.amountPosted * rate.value
                    return BigDecimal.ONE
                } catch (e: Exception) {
                    logger.error("Cannot convert record amount: $record")
                    return@map BigDecimal.ZERO
                }
            } else {
                return@map record.clearedFunds
            }
        }.sumByDecimal { it }
    }
}