package io.ducket.api.domain.controller.operation

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import domain.model.operation.OperationCreateModel
import io.ducket.api.app.DEFAULT_RATE_SCALE
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.app.OperationType
import io.ducket.api.utils.InstantDeserializer
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.Instant

data class OperationCreateDto(
    val transferRate: BigDecimal? = null,
    val transferAccountId: Long? = null,
    val accountId: Long,
    val categoryId: Long,
    val type: OperationType,
    val funds: BigDecimal,
    val description: String? = null,
    val subject: String? = null,
    val notes: String? = null,
    val longitude: BigDecimal? = null,
    val latitude: BigDecimal? = null,
    @JsonDeserialize(using = InstantDeserializer::class) val date: Instant,
) {
    fun validate(): OperationCreateDto {
        org.valiktor.validate(this) {
            validate(OperationCreateDto::transferRate).isPositive().scaleBetween(0, DEFAULT_RATE_SCALE)
            validate(OperationCreateDto::transferAccountId).isPositive()
            validate(OperationCreateDto::accountId).isPositive()
            validate(OperationCreateDto::funds).isPositive().scaleBetween(0, DEFAULT_SCALE)
            validate(OperationCreateDto::categoryId).isPositive()
            validate(OperationCreateDto::description).isNotEmpty()
            validate(OperationCreateDto::subject).isNotEmpty()
            validate(OperationCreateDto::notes).isNotEmpty()
            validate(OperationCreateDto::longitude).scaleBetween(0, 7)
            validate(OperationCreateDto::latitude).scaleBetween(0, 7)
            validate(OperationCreateDto::date).isLessThan(Instant.now())
        }
        return this
    }

    fun toModel(userId: Long,
                importId: Long?,
                clearedFunds: BigDecimal,
    ): OperationCreateModel = OperationCreateModel(
        userId = userId,
        categoryId = categoryId,
        importId = importId,
        transferAccountId = transferAccountId,
        accountId = accountId,
        type = type,
        clearedFunds = clearedFunds,
        postedFunds = funds,
        date = date,
        description = description,
        subject = subject,
        notes = notes,
        latitude = latitude,
        longitude = longitude,
    )
}
