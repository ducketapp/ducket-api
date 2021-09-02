package io.budgery.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import domain.model.transaction.Transaction
import io.budgery.api.domain.controller.label.LabelDto

@JsonInclude(JsonInclude.Include.NON_NULL)
class TransactionDto(@JsonIgnore val transaction: Transaction): RecordDto(transaction) {
    val rule: TransactionRuleDto? = transaction.rule?.let { TransactionRuleDto(transaction.rule) }
    val labels: List<LabelDto> = transaction.labels.map { LabelDto(it) }
}