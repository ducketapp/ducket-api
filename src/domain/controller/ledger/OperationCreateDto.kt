package io.ducket.api.domain.controller.ledger

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.domain.controller.tag.TagCreateDto
import io.ducket.api.utils.InstantDeserializer
import java.math.BigDecimal
import java.time.Instant

data class OperationCreateDto(
    val category: String,
    val categoryGroup: String,
    val description: String? = null,
    val subject: String? = null,
    val notes: String? = null,
    val longitude: BigDecimal? = null,
    val latitude: BigDecimal? = null,
    @JsonDeserialize(using = InstantDeserializer::class) val date: Instant,
)
