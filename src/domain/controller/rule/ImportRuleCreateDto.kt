package io.ducket.api.domain.controller.rule

import io.ducket.api.app.ImportRuleLookupType
import org.valiktor.functions.*

data class ImportRuleCreateDto(
    val name: String,
    val lookupType: ImportRuleLookupType,
    val keywords: List<String>,
    val categoryId: Long,
) {
    fun validate(): ImportRuleCreateDto {
        org.valiktor.validate(this) {
            validate(ImportRuleCreateDto::name).isNotNull().hasSize(1, 64)
            validate(ImportRuleCreateDto::lookupType).isNotNull()
            validate(ImportRuleCreateDto::keywords).isNotNull().isNotEmpty()
            validate(ImportRuleCreateDto::categoryId).isNotNull()
        }
        return this
    }
}