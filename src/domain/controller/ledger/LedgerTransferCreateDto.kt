package io.ducket.api.domain.controller.ledger

import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.Instant

data class LedgerTransferCreateDto(
    val amount: BigDecimal,
    var rate: BigDecimal?,
    val fromAccountId: Long,
    val toAccountId: Long,
    val operation: OperationCreateDto,
) {
    fun validate(): LedgerTransferCreateDto {
        org.valiktor.validate(this) {
            validate(LedgerTransferCreateDto::amount).isPositive().scaleBetween(0, 2)
            validate(LedgerTransferCreateDto::rate).isPositive().scaleBetween(0, 4)
            validate(LedgerTransferCreateDto::fromAccountId).isPositive()
            validate(LedgerTransferCreateDto::toAccountId).isPositive()
            validate(LedgerTransferCreateDto::operation).isNotNull().validate {
                validate(OperationCreateDto::category).isNotNull()
                validate(OperationCreateDto::categoryGroup).isNotNull()
                validate(OperationCreateDto::description).isNotEmpty()
                validate(OperationCreateDto::subject).isNotEmpty()
                validate(OperationCreateDto::notes).isNotEmpty()
                validate(OperationCreateDto::longitude).scaleBetween(0, 7)
                validate(OperationCreateDto::latitude).scaleBetween(0, 7)
                validate(OperationCreateDto::date).isLessThan(Instant.now())
            }
        }
        return this
    }
}