package io.ducket.api.domain.controller.ledger

import io.ducket.api.app.LedgerRecordType
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.Instant

data class LedgerRecordCreateDto(
    var rate: BigDecimal? = null,
    val transferAccountId: Long? = null,
    val transfer: Boolean,
    val amount: BigDecimal,
    val type: LedgerRecordType,
    val accountId: Long,
    val operation: OperationCreateDto,
) {
    fun validate(): LedgerRecordCreateDto {
        org.valiktor.validate(this) {
            if (transfer) {
                validate(LedgerRecordCreateDto::rate).isPositive().scaleBetween(0, 4)
                validate(LedgerRecordCreateDto::transferAccountId).isGreaterThan(0L)
                validate(LedgerRecordCreateDto::type).isEqualTo(LedgerRecordType.EXPENSE)
            } else {
                validate(LedgerRecordCreateDto::type).isNotNull()
                validate(LedgerRecordCreateDto::rate).isNull()
                validate(LedgerRecordCreateDto::transferAccountId).isNull()
            }
            validate(LedgerRecordCreateDto::amount).isPositive().scaleBetween(0, 2)
            validate(LedgerRecordCreateDto::accountId).isGreaterThan(0L)
            validate(LedgerRecordCreateDto::operation).isNotNull().validate {
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
