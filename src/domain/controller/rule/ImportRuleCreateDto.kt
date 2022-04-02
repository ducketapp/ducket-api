package io.ducket.api.domain.controller.rule

import org.valiktor.functions.*

data class ImportRuleCreateDto(
    val name: String,
    val expense: Boolean,
    val income: Boolean,
    val keywords: List<String>,
    val categoryId: Long,
) {
    fun validate(): ImportRuleCreateDto {
        org.valiktor.validate(this) {
            validate(ImportRuleCreateDto::name).isNotNull().hasSize(1, 45)
            validate(ImportRuleCreateDto::expense).isNotNull()
            validate(ImportRuleCreateDto::income).isNotNull()
            validate(ImportRuleCreateDto::keywords).isNotNull().isNotEmpty()
            validate(ImportRuleCreateDto::categoryId).isGreaterThan(0)
        }
        return this
    }
}