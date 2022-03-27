package io.ducket.api.domain.controller.transaction

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.InstantDeserializer
import io.ducket.api.plugins.InvalidDataException
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.Instant

data class TransactionCreateDto(
    var amount: BigDecimal,
    val payeeOrPayer: String? = null,
    val accountId: Long,
    val categoryId: Long,
    val notes: String? = null,
    val longitude: String? = null,
    val latitude: String? = null,
    @JsonDeserialize(using = InstantDeserializer::class) val date: Instant,
) {

    fun validate(): TransactionCreateDto {
        org.valiktor.validate(this) {
            validate(TransactionCreateDto::payeeOrPayer).isNotEmpty()
            validate(TransactionCreateDto::date).isLessThanOrEqualTo(Instant.now())
            validate(TransactionCreateDto::accountId).isNotZero().isPositive()
            validate(TransactionCreateDto::categoryId).isNotZero().isPositive()
            validate(TransactionCreateDto::notes).isNotEmpty()
            validate(TransactionCreateDto::longitude).isNotEmpty()
            validate(TransactionCreateDto::latitude).isNotEmpty()
            validate(TransactionCreateDto::amount).isGreaterThan(BigDecimal.ZERO)

            if (amount.scale() !in 0..2) {
                throw InvalidDataException("Amount scale should not be greater than 2")
            }
        }
        return this
    }
}