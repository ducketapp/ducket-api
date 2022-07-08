package io.ducket.api.domain.controller.rule.dto

import io.ducket.api.app.ImportRuleLookupType
import org.valiktor.functions.*

data class ImportRuleCreateUpdateDto(
    val title: String,
    val lookupType: ImportRuleLookupType,
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