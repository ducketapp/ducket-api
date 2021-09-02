package io.budgery.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.budgery.api.InstantSerializer
import io.budgery.api.domain.controller.category.CategoryDto
import domain.model.transaction.TransactionRule
import java.time.Instant

class TransactionRuleDto(@JsonIgnore val transactionRule: TransactionRule) {
    val id: Int = transactionRule.id
    val userId: Int = transactionRule.id
    val forExpenses: Boolean = transactionRule.forExpenses
    val forIncomes: Boolean = transactionRule.forIncomes
    val newCategory: CategoryDto = CategoryDto(transactionRule.newCategory)
    val name: String = transactionRule.name
    val keywords: List<String> = transactionRule.keywords
    val priority: Int = transactionRule.priority
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = transactionRule.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = transactionRule.modifiedAt
}