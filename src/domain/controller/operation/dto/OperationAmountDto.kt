package org.expenny.service.domain.controller.operation.dto

import java.math.BigDecimal

data class OperationAmountDto(
    val posted: BigDecimal,
    val cleared: BigDecimal,
)
