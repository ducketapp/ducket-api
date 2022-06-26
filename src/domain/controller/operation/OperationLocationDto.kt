package io.ducket.api.domain.controller.operation

import java.math.BigDecimal

data class OperationLocationDto(
    val longitude: BigDecimal,
    val latitude: BigDecimal,
)
