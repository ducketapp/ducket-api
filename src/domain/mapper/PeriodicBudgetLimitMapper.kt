package io.ducket.api.domain.mapper

import io.ducket.api.domain.model.periodic_budget.PeriodicBudgetLimit
import io.ducket.api.domain.model.periodic_budget.PeriodicBudgetLimitCreate
import io.ducket.api.domain.model.periodic_budget.PeriodicBudgetLimitUpdate
import io.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetLimitCreateDto
import io.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetLimitDto
import io.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetLimitUpdateDto

object PeriodicBudgetLimitMapper {

    fun mapDtoToModel(dto: PeriodicBudgetLimitCreateDto, budgetId: Long, default: Boolean): PeriodicBudgetLimitCreate {
        return DataClassMapper<PeriodicBudgetLimitCreateDto, PeriodicBudgetLimitCreate>()
            .provide(PeriodicBudgetLimitCreate::budgetId, budgetId)
            .provide(PeriodicBudgetLimitCreate::default, default)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: PeriodicBudgetLimitUpdateDto): PeriodicBudgetLimitUpdate {
        return DataClassMapper<PeriodicBudgetLimitUpdateDto, PeriodicBudgetLimitUpdate>().invoke(dto)
    }

    fun mapModelToDto(model: PeriodicBudgetLimit): PeriodicBudgetLimitDto {
        return DataClassMapper<PeriodicBudgetLimit, PeriodicBudgetLimitDto>().invoke(model)
    }
}