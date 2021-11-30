package io.ducket.api.domain.controller.transaction

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.InstantDeserializer
import io.ducket.api.plugins.InvalidDataError
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.Instant

data class TransactionCreateDto(
    var amount: BigDecimal,
    val payee: String,
    val accountId: String,
    val categoryId: String,
    val notes: String?,
    val longitude: String?,
    val latitude: String?,
    @JsonDeserialize(using = InstantDeserializer::class) val date: Instant,
) {
    fun validate(): TransactionCreateDto {
        org.valiktor.validate(this) {
            validate(TransactionCreateDto::payee).isNotEmpty()
            validate(TransactionCreateDto::date).isLessThanOrEqualTo(Instant.now())
            validate(TransactionCreateDto::accountId).isNotEmpty()
            validate(TransactionCreateDto::categoryId).isNotEmpty()
            validate(TransactionCreateDto::notes).isNotEmpty()
            validate(TransactionCreateDto::longitude).isNotEmpty()
            validate(TransactionCreateDto::latitude).isNotEmpty()
            validate(TransactionCreateDto::amount).isNotEqualTo(BigDecimal.ZERO)

            if (amount.scale() !in 0..2) {
                throw InvalidDataError("Transaction amount scale should not be greater than 2")
            }
        }
        return this
    }
}