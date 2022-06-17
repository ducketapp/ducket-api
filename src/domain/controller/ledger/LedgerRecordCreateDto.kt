package io.ducket.api.domain.controller.ledger

import io.ducket.api.app.LedgerRecordType
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.Instant

data class LedgerRecordCreateDto(
    var rate: BigDecimal? = null,
    val transferAccountId: Long? = null,
    val amount: BigDecimal,
    val type: LedgerRecordType,
    val accountId: Long,
    val operation: OperationCreateDto,
) {
    fun validate(): LedgerRecordCreateDto {
        org.valiktor.validate(this) {
            if (transferAccountId != null) {
                validate(LedgerRecordCreateDto::rate).isNotNull().isPositive().scaleBetween(0, 4)
                validate(LedgerRecordCreateDto::transferAccountId).isNotNull().isPositive()
                validate(LedgerRecordCreateDto::type).isNotNull().isEqualTo(LedgerRecordType.EXPENSE)
            } else {
                validate(LedgerRecordCreateDto::rate).isNull()
            }

            validate(LedgerRecordCreateDto::amount).isPositive().scaleBetween(0, 2)
            validate(LedgerRecordCreateDto::accountId).isPositive()
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
