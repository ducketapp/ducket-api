package org.expenny.service.domain.controller.operation.dto

import java.math.BigDecimal

data class OperationLocationDto(
    val longitude: BigDecimal,
    val latitude: BigDecimal,
)
