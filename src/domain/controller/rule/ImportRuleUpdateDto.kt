package io.ducket.api.domain.controller.rule

import org.valiktor.functions.*

data class ImportRuleUpdateDto(
    val name: String?,
    val expense: Boolean?,
    val income: Boolean?,
    val keywords: List<String>?,
    val categoryId: Long?,
) {
    fun validate(): ImportRuleUpdateDto {
        org.valiktor.validate(this) {
            validate(ImportRuleUpdateDto::name).isNotNull().hasSize(1, 45)
            validate(ImportRuleUpdateDto::expense).isNotNull()
            validate(ImportRuleUpdateDto::income).isNotNull()
            validate(ImportRuleUpdateDto::keywords).isNotNull().isNotEmpty()
            validate(ImportRuleUpdateDto::categoryId).isNotNull().isGreaterThan(0)
        }
        return this
    }
}
