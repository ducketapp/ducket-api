package io.budgery.api.domain.controller.transaction

import org.valiktor.functions.*

class TransactionDeleteDto(
    val ids: List<Int>?,
) {
    fun validate(): TransactionDeleteDto {
        org.valiktor.validate(this) {
            validate(TransactionDeleteDto::ids).isNotNull().isNotEmpty()
        }
        return this
    }
}