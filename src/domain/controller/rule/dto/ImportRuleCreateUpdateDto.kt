package dev.ducket.api.domain.controller.rule.dto

import dev.ducket.api.app.ImportRuleApplyType
import org.valiktor.functions.*

data class ImportRuleCreateUpdateDto(
    val title: String,
    val lookupType: ImportRuleApplyType,
    val keywords: List<String>,
    val categoryId: Long,
) {
    fun validate(): ImportRuleCreateUpdateDto {
        org.valiktor.validate(this) {
            validate(ImportRuleCreateUpdateDto::title).isNotNull().hasSize(1, 64)
            validate(ImportRuleCreateUpdateDto::keywords).isNotEmpty()
            validate(ImportRuleCreateUpdateDto::categoryId).isPositive()
        }
        return this
    }
}