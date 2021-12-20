package io.ducket.api.domain.controller.imports

import java.math.BigDecimal
import java.time.Instant

data class CsvTransactionDto(
    val date: Instant,
    var category: String,
    val beneficiaryOrSender: String,
    val description: String,
    val amount: BigDecimal,
)