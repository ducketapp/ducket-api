package io.ducket.api.domain.controller.transaction

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import domain.model.rule.Rule
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.category.TypelessCategoryDto
import java.time.Instant

class RuleDto(@JsonIgnore val rule: Rule) {
    val id: String = rule.id
    val name: String = rule.name
    val categoryToApply: TypelessCategoryDto = TypelessCategoryDto(rule.recordCategory)
    val keywords: List<String> = rule.keywords
    val applyToExpense: Boolean = rule.isExpense
    val applyToIncome: Boolean = rule.isIncome
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = rule.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = rule.modifiedAt
}