package io.ducket.api.domain.controller.rule

import org.valiktor.functions.*

class ImportRuleCreateDto(
    val name: String,
    val expense: Boolean = true,
    val income: Boolean = true,
    val keywords: String,
    val categoryId: Long,
) {
    fun validate(): ImportRuleCreateDto {
        org.valiktor.validate(this) {
            validate(ImportRuleCreateDto::name).isNotNull().hasSize(1, 45)
            validate(ImportRuleCreateDto::keywords).isNotBlank()
            validate(ImportRuleCreateDto::categoryId).isPositive().isNotZero()
        }
        return this
    }
}