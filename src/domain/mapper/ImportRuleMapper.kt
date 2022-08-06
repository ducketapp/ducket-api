package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.category.Category
import dev.ducketapp.service.domain.model.imports.ImportRule
import dev.ducketapp.service.domain.model.imports.ImportRuleCreate
import dev.ducketapp.service.domain.model.imports.ImportRuleUpdate
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto
import dev.ducketapp.service.domain.controller.rule.dto.ImportRuleCreateUpdateDto
import dev.ducketapp.service.domain.controller.rule.dto.ImportRuleDto

object ImportRuleMapper {

    fun mapDtoToModel(dto: ImportRuleCreateUpdateDto, userId: Long): ImportRuleCreate {
        return DataClassMapper<ImportRuleCreateUpdateDto, ImportRuleCreate>()
            .provide(ImportRuleCreate::userId, userId)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: ImportRuleCreateUpdateDto): ImportRuleUpdate {
        return DataClassMapper<ImportRuleCreateUpdateDto, ImportRuleUpdate>().invoke(dto)
    }

    fun mapModelToDto(model: ImportRule): ImportRuleDto {
        return DataClassMapper<ImportRule, ImportRuleDto>()
            .register(ImportRuleDto::category, DataClassMapper<Category, CategoryDto>())
            .invoke(model)
    }
}