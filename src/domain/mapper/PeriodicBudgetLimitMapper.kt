package domain.mapper

import domain.controller.periodic_budget.dto.PeriodicBudgetLimitCreateDto
import domain.controller.periodic_budget.dto.PeriodicBudgetLimitDto
import domain.controller.periodic_budget.dto.PeriodicBudgetLimitUpdateDto
import domain.mapper.DataClassMapper
import domain.model.periodic_budget.PeriodicBudgetLimit
import domain.model.periodic_budget.PeriodicBudgetLimitCreate
import domain.model.periodic_budget.PeriodicBudgetLimitUpdate

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