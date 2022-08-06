package dev.ducketapp.service.domain.controller.rule.dto

import dev.ducketapp.service.app.ImportRuleApplyType
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto

data class ImportRuleDto(
    val id: Long,
    val name: String,
    val lookupType: ImportRuleApplyType,
    val keywords: List<String>,
    val category: CategoryDto,
)