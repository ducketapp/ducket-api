package io.ducket.api.domain.controller.rule.dto

import domain.model.imports.ImportRule
import io.ducket.api.app.ImportRuleLookupType
import io.ducket.api.domain.controller.category.dto.CategoryDto

data class ImportRuleDto(
    val id: Long,
    val name: String,
    val lookupType: ImportRuleLookupType,
    val keywords: List<String>,
    val category: CategoryDto,
)