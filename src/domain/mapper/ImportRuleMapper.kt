package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.category.Category
import org.expenny.service.domain.model.imports.ImportRule
import org.expenny.service.domain.model.imports.ImportRuleCreate
import org.expenny.service.domain.model.imports.ImportRuleUpdate
import org.expenny.service.domain.controller.category.dto.CategoryDto
import org.expenny.service.domain.controller.rule.dto.ImportRuleCreateUpdateDto
import org.expenny.service.domain.controller.rule.dto.ImportRuleDto

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