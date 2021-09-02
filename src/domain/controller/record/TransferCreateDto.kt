package io.budgery.api.domain.controller.record

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.budgery.api.InstantDeserializer
import org.valiktor.functions.*
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.time.Instant

data class TransferCreateDto(
    var amount: BigDecimal,
    val payee: String,
    val accountId: Int,
    val transferAccountId: Int,
    val categoryId: Int,
    val labelIds: List<Int>?,
    val note: String?,
    val longitude: String?,
    val latitude: String?,
    val exchangeRate: Double,
    @JsonDeserialize(using = InstantDeserializer::class) val date: Instant,
) {
    fun validate(): TransferCreateDto {
        org.valiktor.validate(this) {
            validate(TransferCreateDto::exchangeRate).isGreaterThanOrEqualTo(1.0)
            validate(TransferCreateDto::payee).isNotEmpty()
            validate(TransferCreateDto::date).isLessThanOrEqualTo(Instant.now())
            validate(TransferCreateDto::accountId).isGreaterThan(0)
            validate(TransferCreateDto::transferAccountId).isGreaterThan(0)
            validate(TransferCreateDto::categoryId).isGreaterThan(0)
            validate(TransferCreateDto::labelIds).isValid { !it.contains(0) }
            validate(TransferCreateDto::note).isNotEmpty()
            validate(TransferCreateDto::longitude).isNotEmpty()
            validate(TransferCreateDto::latitude).isNotEmpty()
            validate(TransferCreateDto::amount).isLessThan(BigDecimal.ZERO)

            if (amount.scale() !in 0..2) {
                throw IllegalArgumentException("Transfer amount scale should not be greater than 2")
            }

            if (accountId == transferAccountId) {
                throw IllegalArgumentException("Affected and transfer accounts should differ")
            }
        }
        return this
    }
}