package io.ducket.api.domain.service

import io.ducket.api.CurrencyRateProvider
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.budget.BudgetCreateDto
import io.ducket.api.domain.controller.budget.BudgetDto
import io.ducket.api.domain.controller.budget.BudgetProgressDto
import io.ducket.api.domain.model.budget.Budget
import io.ducket.api.domain.model.budget.BudgetEntity
import io.ducket.api.domain.model.ledger.LedgerRecord
import io.ducket.api.domain.repository.*
import io.ducket.api.utils.lt
import io.ducket.api.getLogger
import io.ducket.api.plugins.DuplicateEntityException
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import io.ducket.api.utils.isAfterInclusive
import io.ducket.api.utils.isBeforeInclusive
import io.ducket.api.utils.sumByDecimal
import org.koin.java.KoinJavaComponent.inject
import java.math.BigDecimal
import java.time.ZoneId

class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val ledgerRepository: LedgerRepository,
    private val groupService: GroupService,
) {
    private val logger = getLogger()
    private val currencyRateProvider: CurrencyRateProvider by inject(CurrencyRateProvider::class.java)

    fun createBudget(userId: Long, payload: BudgetCreateDto): BudgetDto {
        if (payload.fromDate.isAfter(payload.toDate)) {
            throw InvalidDataException("Invalid time frames")
        }

        budgetRepository.findOneByName(userId, payload.name)?.let {
            throw DuplicateEntityException()
        }

        return budgetRepository.create(userId, payload).let {
            BudgetDto(
                budget = BudgetEntity[it.id].toModel(),
                progress = resolveBudgetProgress(it),
            )
        }
    }

    fun getBudgetAccessibleToUser(userId: Long, budgetId: Long): BudgetDto {
        return getBudgetsAccessibleToUser(userId).firstOrNull { it.id == budgetId } ?: throw NoEntityFoundException()
    }

    fun getBudgetsAccessibleToUser(userId: Long): List<BudgetDto> {
        val userIdsWithMutualGroupMemberships = groupService.getActiveMembersFromSharedUserGroups(userId).map { it.id }

        return budgetRepository.findAll(*userIdsWithMutualGroupMemberships.toLongArray(), userId).map {
            return@map BudgetDto(
                budget = it,
                progress = resolveBudgetProgress(it),
            )
        }
    }

    fun deleteBudgets(userId: Long, payload: BulkDeleteDto) {
        budgetRepository.delete(userId, *payload.ids.toLongArray())
    }

    fun deleteBudget(userId: Long, budgetId: Long) {
        budgetRepository.delete(userId, budgetId)
    }

    private fun resolveBudgetProgress(budget: Budget): BudgetProgressDto {
        val ledgerRecords = ledgerRepository.findAll(budget.user.id)

        val affectedLedgerRecords = ledgerRecords.filter { record ->
            record.operation.date.isAfterInclusive(budget.fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && record.operation.date.isBeforeInclusive(budget.toDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    && budget.accounts.map { it.id }.contains(record.account.id)
                    && budget.categories.map { it.id }.contains(record.operation.category.id)
        }

        return resolveRecordsTotalBalance(affectedLedgerRecords, budget.currency.isoCode).let { amount ->
            BudgetProgressDto().also {
                if (amount.lt(BigDecimal.ZERO)) {
                    it.percentage = amount.abs() * BigDecimal(100) / budget.limit
                    it.amount = amount.abs()
                }
            }
        }
    }

    private fun resolveRecordsTotalBalance(records: List<LedgerRecord>, currencyIsoCode: String): BigDecimal {
        return records.map { record ->
            val recordCurrencyIsoCode = record.account.currency.isoCode

            if (recordCurrencyIsoCode != currencyIsoCode) {
                try {
                    val rate = currencyRateProvider.getCurrencyRate(base = recordCurrencyIsoCode, term = currencyIsoCode)
                    return@map record.amountPosted * rate
                } catch (e: CurrencyRateProvider.CurrencyRateClientException) {
                    logger.error("Cannot convert record amount: $record")
                    return@map BigDecimal.ZERO
                }
            } else {
                return@map record.amountPosted
            }
        }.sumByDecimal { it }
    }
}