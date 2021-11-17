package io.ducket.api.domain.controller.imports

import java.math.BigDecimal
import java.time.Instant

data class CsvTransaction(
    val date: Instant,
    var category: String,
    val beneficiaryOrSender: String,
    val notes: String,
    val amount: BigDecimal,
)