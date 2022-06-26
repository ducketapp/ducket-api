package io.ducket.api.domain.controller.imports

import io.ducket.api.app.OperationType
import java.math.BigDecimal
import java.time.Instant

data class CsvRecordDto(
    val date: Instant,
    var category: String,
    val subject: String,
    val description: String,
    val notes: String,
    val type: OperationType,
    val amount: BigDecimal,
)