package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.category.Category
import dev.ducket.api.domain.model.imports.ImportRule
import dev.ducket.api.domain.model.imports.ImportRuleCreate
import dev.ducket.api.domain.model.imports.ImportRuleUpdate
import dev.ducket.api.domain.controller.category.dto.CategoryDto
import dev.ducket.api.domain.controller.rule.dto.ImportRuleCreateUpdateDto
import dev.ducket.api.domain.controller.rule.dto.ImportRuleDto

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