package io.ducket.api.domain.controller.imports.dto

import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate

data class OperationImportDto(
    val date: LocalDate,
    val subject: String?,
    val amount: BigDecimal,
    val currency: String,
    val description: String?,
    val notes: String?,
) {
    fun validate(): OperationImportDto {
        org.valiktor.validate(this) {
            validate(OperationImportDto::amount).scaleBetween(0, DEFAULT_SCALE)
            validate(OperationImportDto::subject).isNotBlank()
            validate(OperationImportDto::currency).isNotBlank().hasSize(3, 3)
            validate(OperationImportDto::description).isNotBlank()
            validate(OperationImportDto::notes).isNotBlank()
        }
        return this
    }
}