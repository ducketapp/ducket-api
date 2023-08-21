package org.expenny.service.domain.service

import org.expenny.service.app.database.Transactional
import org.expenny.service.domain.mapper.BudgetMapper
import org.expenny.service.domain.controller.BulkDeleteDto
import org.expenny.service.domain.controller.budget.dto.BudgetCreateDto
import org.expenny.service.domain.controller.budget.dto.BudgetDto
import org.expenny.service.domain.controller.budget.dto.BudgetUpdateDto
import org.expenny.service.domain.repository.BudgetAccountRepository
import org.expenny.service.domain.repository.BudgetRepository
import org.expenny.service.plugins.DuplicateDataException
import org.expenny.service.plugins.NoDataFoundException

class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val budgetAccountRepository: BudgetAccountRepository,
): Transactional {

    suspend fun createBudget(userId: Long, dto: BudgetCreateDto): BudgetDto {
        budgetRepository.findOneByTitle(userId, dto.title)?.also { throw DuplicateDataException() }

        return blockingTransaction {
            budgetRepository.createBudget(BudgetMapper.mapDtoToModel(dto, userId)).also { budget ->
                budgetAccountRepository.create(budget.id, *dto.accountIds.toLongArray())
            }
        }.let { budget ->
            getBudget(userId, budget.id)
        }
    }

    suspend fun updateBudget(userId: Long, budgetId: Long, dto: BudgetUpdateDto): BudgetDto {
        budgetRepository.findOneByTitle(userId, dto.title)?.takeIf { it.id != budgetId }?.also {
            throw DuplicateDataException()
        }

        return blockingTransaction {
            budgetRepository.updateBudget(userId, budgetId, BudgetMapper.mapDtoToModel(dto))?.also { budget ->
                budgetAccountRepository.deleteAllByBudget(budgetId)
                budgetAccountRepository.create(budget.id, *dto.accountIds.toLongArray())
            }
        }?.let { budget ->
            BudgetMapper.mapModelToDto(budget)
        } ?: throw NoDataFoundException()
    }

    suspend fun getBudget(userId: Long, budgetId: Long): BudgetDto {
        return budgetRepository.findOne(userId, budgetId)?.let {
            BudgetMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getBudgets(userId: Long): List<BudgetDto> {
        return budgetRepository.findAll(userId).map {
            BudgetMapper.mapModelToDto(it)
        }
    }

    suspend fun deleteBudgets(userId: Long, payload: BulkDeleteDto) {
        budgetRepository.delete(userId, *payload.ids.toLongArray())
    }

    suspend fun deleteBudget(userId: Long, budgetId: Long) {
        budgetRepository.delete(userId, budgetId)
    }
}