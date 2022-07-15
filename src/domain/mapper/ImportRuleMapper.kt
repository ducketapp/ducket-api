package io.ducket.api.domain.mapper

import io.ducket.api.domain.model.category.Category
import io.ducket.api.domain.model.imports.ImportRule
import io.ducket.api.domain.model.imports.ImportRuleCreate
import io.ducket.api.domain.model.imports.ImportRuleUpdate
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.rule.dto.ImportRuleCreateUpdateDto
import io.ducket.api.domain.controller.rule.dto.ImportRuleDto

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