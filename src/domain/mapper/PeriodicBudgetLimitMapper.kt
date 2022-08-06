package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetLimit
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetLimitCreate
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetLimitUpdate
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitCreateDto
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitDto
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitUpdateDto

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