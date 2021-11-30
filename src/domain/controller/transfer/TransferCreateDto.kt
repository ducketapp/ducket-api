package io.ducket.api.domain.controller.transfer

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.InstantDeserializer
import io.ducket.api.plugins.InvalidDataError
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.Instant

data class TransferCreateDto(
    var amount: BigDecimal,
    val accountId: String,
    val transferAccountId: String,
    val note: String?,
    val longitude: String?,
    val latitude: String?,
    var exchangeRate: BigDecimal?,
    @JsonDeserialize(using = InstantDeserializer::class) var date: Instant,
) {
    fun validate(): TransferCreateDto {
        org.valiktor.validate(this) {
            validate(TransferCreateDto::exchangeRate).isGreaterThan(BigDecimal.ZERO)
            validate(TransferCreateDto::date).isLessThanOrEqualTo(Instant.now())
            validate(TransferCreateDto::accountId).isNotBlank()
            validate(TransferCreateDto::transferAccountId).isNotBlank()
            validate(TransferCreateDto::note).isNotEmpty()
            validate(TransferCreateDto::longitude).isNotEmpty()
            validate(TransferCreateDto::latitude).isNotEmpty()
            validate(TransferCreateDto::amount).isLessThan(BigDecimal.ZERO)

            if (amount.scale() !in 0..2) {
                throw InvalidDataError("Transfer amount scale should not be greater than 2")
            }

            if (accountId == transferAccountId) {
                throw InvalidDataError("Origin and target transfer accounts should differ")
            }
        }
        return this
    }
}