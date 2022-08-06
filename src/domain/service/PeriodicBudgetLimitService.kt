package dev.ducketapp.service.domain.service

import dev.ducketapp.service.app.PeriodicBudgetType
import dev.ducketapp.service.domain.mapper.PeriodicBudgetLimitMapper
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitCreateDto
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitDto
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitUpdateDto
import dev.ducketapp.service.domain.repository.PeriodicBudgetLimitRepository
import dev.ducketapp.service.domain.repository.PeriodicBudgetRepository
import dev.ducketapp.service.plugins.DuplicateDataException
import dev.ducketapp.service.plugins.InvalidDataException
import dev.ducketapp.service.plugins.NoDataFoundException
import org.threeten.extra.*
import java.time.*
import java.time.temporal.IsoFields


// Limit change logic: if limit is default -> create new for a given period, else -> update existing
class PeriodicBudgetLimitService(
    private val periodicBudgetRepository: PeriodicBudgetRepository,
    private val periodicBudgetLimitRepository: PeriodicBudgetLimitRepository
) {

    suspend fun createLimit(userId: Long, budgetId: Long, dto: PeriodicBudgetLimitCreateDto): PeriodicBudgetLimitDto {
        val budget = periodicBudgetRepository.findOne(userId, budgetId) ?: throw NoDataFoundException("No such budget was found")

        when (budget.periodType) {
            PeriodicBudgetType.DAILY -> {
                LocalDateRange.of(dto.fromDate, dto.fromDate)
            }
            PeriodicBudgetType.WEEKLY -> {
                YearWeek.of(dto.fromDate.year, dto.fromDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)).let {
                    LocalDateRange.of(it.atDay(DayOfWeek.MONDAY), it.atDay(DayOfWeek.SUNDAY))
                }
            }
            PeriodicBudgetType.MONTHLY -> {
                YearMonth.of(dto.fromDate.year, dto.fromDate.month).let {
                    LocalDateRange.of(it.atDay(1), it.atEndOfMonth())
                }
            }
            PeriodicBudgetType.QUARTERLY -> {
                YearQuarter.of(dto.fromDate.year, dto.fromDate.get(IsoFields.QUARTER_OF_YEAR)).let {
                    LocalDateRange.of(it.atDay(1), it.atEndOfQuarter())
                }
            }
            PeriodicBudgetType.ANNUALLY -> {
                Year.of(dto.fromDate.year).let {
                    LocalDateRange.of(it.atDay(1), it.atMonth(Month.DECEMBER).atEndOfMonth())
                }
            }
        }.takeIf {
            it.equals(LocalDateRange.of(dto.fromDate, dto.toDate))
        } ?: throw InvalidDataException("Invalid date range")

        periodicBudgetLimitRepository.findOneByBudgetAndPeriod(budgetId, dto.fromDate, dto.toDate)?.also {
            if (!it.default) {
                throw DuplicateDataException("Budgets limit for such a period already defined")
            }
        }

        return PeriodicBudgetLimitMapper.mapDtoToModel(dto, budgetId, false).let {
            periodicBudgetLimitRepository.create(it).let { budgetLimit ->
                PeriodicBudgetLimitMapper.mapModelToDto(budgetLimit)
            }
        }
    }

    suspend fun updateLimit(userId: Long, budgetId: Long, limitId: Long, dto: PeriodicBudgetLimitUpdateDto): PeriodicBudgetLimitDto {
        periodicBudgetLimitRepository.findOneByIdAndBudget(userId, budgetId, limitId) ?: throw NoDataFoundException()

        return PeriodicBudgetLimitMapper.mapDtoToModel(dto).let {
            periodicBudgetLimitRepository.update(limitId, it)?.let { budgetLimit ->
                PeriodicBudgetLimitMapper.mapModelToDto(budgetLimit)
            } ?: throw NoDataFoundException()
        }
    }

    suspend fun getLimit(userId: Long, budgetId: Long, limitId: Long): PeriodicBudgetLimitDto {
        return periodicBudgetLimitRepository.findOneByIdAndBudget(userId, budgetId, limitId)?.let {
            PeriodicBudgetLimitMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getLimits(userId: Long, budgetId: Long): List<PeriodicBudgetLimitDto> {
        periodicBudgetRepository.findOne(userId, budgetId) ?: throw NoDataFoundException("No such budget was found")

        return periodicBudgetLimitRepository.findAllByBudget(budgetId).map {
            PeriodicBudgetLimitMapper.mapModelToDto(it)
        }
    }
}