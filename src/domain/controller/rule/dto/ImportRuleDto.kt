package org.expenny.service.domain.controller.rule.dto

import org.expenny.service.app.ImportRuleApplyType
import org.expenny.service.domain.controller.category.dto.CategoryDto

data class ImportRuleDto(
    val id: Long,
    val name: String,
    val lookupType: ImportRuleApplyType,
    val keywords: List<String>,
    val category: CategoryDto,
)