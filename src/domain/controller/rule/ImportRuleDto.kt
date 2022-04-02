package io.ducket.api.domain.controller.rule

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import domain.model.imports.ImportRule
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.category.TypelessCategoryDto
import java.time.Instant

data class ImportRuleDto(
    val id: Long,
    val name: String,
    val categoryToApply: TypelessCategoryDto,
    val keywords: List<String>,
    val applyToExpense: Boolean,
    val applyToIncome: Boolean,
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant,
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant,
) {
    constructor(rule: ImportRule): this(
        id = rule.id,
        name = rule.name,
        categoryToApply = TypelessCategoryDto(rule.recordCategory),
        keywords = rule.keywords,
        applyToExpense = rule.isExpense,
        applyToIncome = rule.isIncome,
        createdAt = rule.createdAt,
        modifiedAt = rule.modifiedAt,
    )
}