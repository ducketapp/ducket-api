package io.ducket.api.domain.controller.rule.dto

import io.ducket.api.app.ImportRuleApplyType
import io.ducket.api.domain.controller.category.dto.CategoryDto

data class ImportRuleDto(
    val id: Long,
    val name: String,
    val lookupType: ImportRuleApplyType,
    val keywords: List<String>,
    val category: CategoryDto,
)