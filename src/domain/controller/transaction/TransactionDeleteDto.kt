package io.ducket.api.domain.controller.transaction

import org.valiktor.functions.*

data class TransactionDeleteDto(
    val transactionIds: List<Long>,
) {

    fun validate(): TransactionDeleteDto {
        org.valiktor.validate(this) {
            validate(TransactionDeleteDto::transactionIds).isNotNull().isNotEmpty()
        }
        return this
    }
}