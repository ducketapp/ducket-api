package io.ducket.api.domain.controller.transaction

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import domain.model.transaction.Transaction
import io.ducket.api.domain.controller.imports.ImportDto
import io.ducket.api.domain.controller.record.RecordDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TransactionDto(@JsonIgnore val transaction: Transaction): RecordDto(transaction) {
    val import: ImportDto? = transaction.import?.let { ImportDto(it) }
    val payeeOrPayer: String = transaction.payeeOrPayer
}