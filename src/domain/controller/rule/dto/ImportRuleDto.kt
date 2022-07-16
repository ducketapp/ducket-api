package dev.ducket.api.domain.controller.rule.dto

import dev.ducket.api.app.ImportRuleApplyType
import dev.ducket.api.domain.controller.category.dto.CategoryDto

data class ImportRuleDto(
    val id: Long,
    val name: String,
    val lookupType: ImportRuleApplyType,
    val keywords: List<String>,
    val category: CategoryDto,
)