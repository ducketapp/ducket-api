package org.expenny.service.domain.service

import org.expenny.service.app.database.Transactional
import org.expenny.service.domain.controller.BulkDeleteDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetCreateDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetUpdateDto
import org.expenny.service.domain.mapper.PeriodicBudgetMapper
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetLimitCreate
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetLimitUpdate
import org.expenny.service.domain.repository.*
import org.expenny.service.plugins.DuplicateDataException
import org.expenny.service.plugins.InvalidDataException
import org.expenny.service.plugins.NoDataFoundException
import org.expenny.service.utils.*


class PeriodicBudgetService(
    private val periodicBudgetRepository: PeriodicBudgetRepository,
    private val periodicBudgetLimitRepository: PeriodicBudgetLimitRepository,
    private val periodicBudgetAccountRepository: PeriodicBudgetAccountRepository,
): Transactional {

    suspend fun createBudget(userId: Long, dto: PeriodicBudgetCreateDto): PeriodicBudgetDto {
        periodicBudgetRepository.findOneByTitle(userId, dto.title)?.also { throw DuplicateDataException() }

        val startPeriodDateRange = dto.startDate.getPeriodDateRange(dto.periodType)

        return blockingTransaction {
            periodicBudgetRepository.create(PeriodicBudgetMapper.mapDtoToModel(dto, userId)).also { budget ->
                periodicBudgetLimitRepository.create(
                    PeriodicBudgetLimitCreate(
                        budgetId = budget.id,
                        limit = dto.limit,
                        fromDate = startPeriodDateRange.start,
                        toDate = startPeriodDateRange.end,
                        default = true,
                    )
                )
                periodicBudgetAccountRepository.create(budget.id, *dto.accountIds.toLongArray())
            }
        }.let { budget ->
            getBudget(userId, budget.id)
        }
    }

    suspend fun updateBudget(userId: Long, budgetId: Long, dto: PeriodicBudgetUpdateDto): PeriodicBudgetDto {
        periodicBudgetRepository.findOneByTitle(userId, dto.title)?.takeIf { it.id != budgetId }?.also {
            throw DuplicateDataException()
        }

        periodicBudgetRepository.findOne(userId, budgetId)?.also {
            if (it.closeDate != null && dto.closeDate != null) {
                throw InvalidDataException("Cannot update closed budget")
            }
        } ?: throw NoDataFoundException()

        dto.closeDate?.takeIf { it.isBefore(dto.startDate) }?.also {
            throw IllegalArgumentException("Close date cannot be earlier than start date")
        }

        val startPeriodDateRange = dto.startDate.getPeriodDateRange(dto.periodType)

        return blockingTransaction {
            periodicBudgetRepository.update(userId, budgetId, PeriodicBudgetMapper.mapDtoToModel(dto))?.also { budget ->
                val defaultBudgetLimit = periodicBudgetLimitRepository.findDefaultByBudget(budgetId)!!

                periodicBudgetLimitRepository.update(
                    limitId = defaultBudgetLimit.id,
                    data = PeriodicBudgetLimitUpdate(
                        limit = dto.limit,
                        fromDate = startPeriodDateRange.start,
                        toDate = startPeriodDateRange.end
                    )
                )

                periodicBudgetAccountRepository.deleteAllByBudget(budget.id)
                periodicBudgetAccountRepository.create(budget.id, *dto.accountIds.toLongArray())
            }
        }?.let { budget ->
            getBudget(userId, budget.id)
        } ?: throw NoDataFoundException()
    }

    suspend fun getBudget(userId: Long, budgetId: Long): PeriodicBudgetDto {
        return periodicBudgetRepository.findOne(userId, budgetId)?.let {
            PeriodicBudgetMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getBudgets(userId: Long): List<PeriodicBudgetDto> {
        return periodicBudgetRepository.findAll(userId).map {
            PeriodicBudgetMapper.mapModelToDto(it)
        }
    }

    suspend fun deleteBudgets(userId: Long, payload: BulkDeleteDto) {
        periodicBudgetRepository.delete(userId, *payload.ids.toLongArray())
    }

    suspend fun deleteBudget(userId: Long, budgetId: Long) {
        periodicBudgetRepository.delete(userId, budgetId)
    }
}