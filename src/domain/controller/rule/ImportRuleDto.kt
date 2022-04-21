package io.ducket.api.domain.controller.rule

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import domain.model.imports.ImportRule
import io.ducket.api.app.ImportRuleLookupType
import io.ducket.api.utils.InstantSerializer
import io.ducket.api.domain.controller.category.TypelessCategoryDto
import java.time.Instant

data class ImportRuleDto(
    val id: Long,
    val name: String,
    val lookupType: ImportRuleLookupType,
    val keywords: List<String>,
    val category: TypelessCategoryDto,
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant,
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant,
) {
    constructor(rule: ImportRule) : this(
        id = rule.id,
        name = rule.name,
        lookupType = rule.lookupType,
        keywords = rule.keywords,
        category = TypelessCategoryDto(rule.category),
        createdAt = rule.createdAt,
        modifiedAt = rule.modifiedAt,
    )
}