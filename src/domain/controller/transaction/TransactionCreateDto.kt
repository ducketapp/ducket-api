package io.budgery.api.domain.controller.transaction

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.budgery.api.InstantDeserializer
import org.valiktor.functions.*
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.time.Instant

class TransactionCreateDto(
    var amount: BigDecimal,
    val payee: String,
    val accountId: Int,
    val categoryId: Int,
    val labelIds: List<Int>?,
    val note: String?,
    val longitude: String?,
    val latitude: String?,
    @JsonDeserialize(using = InstantDeserializer::class) val date: Instant,
) {
    fun validate(): TransactionCreateDto {
        org.valiktor.validate(this) {
            validate(TransactionCreateDto::payee).isNotEmpty()
            validate(TransactionCreateDto::date).isLessThanOrEqualTo(Instant.now())
            validate(TransactionCreateDto::accountId).isGreaterThan(0)
            validate(TransactionCreateDto::categoryId).isGreaterThan(0)
            validate(TransactionCreateDto::labelIds).isValid { !it.contains(0) }
            validate(TransactionCreateDto::note).isNotEmpty()
            validate(TransactionCreateDto::longitude).isNotEmpty()
            validate(TransactionCreateDto::latitude).isNotEmpty()
            validate(TransactionCreateDto::amount).isNotEqualTo(BigDecimal.ZERO)

            if (amount.scale() !in 0..2) {
                throw IllegalArgumentException("Transaction amount scale should not be greater than 2")
            }
        }
        return this
    }
}