package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.utils.LocalDateDeserializer
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate


data class BudgetCreateDto(
    val threshold: BigDecimal,
    val name: String,
    val notes: String?,
    val currencyIsoCode: String,
    val accountIds: List<Long>,
    val categoryIds: List<Long>,
    @JsonDeserialize(using = LocalDateDeserializer::class) val fromDate: LocalDate,
    @JsonDeserialize(using = LocalDateDeserializer::class) val toDate: LocalDate,
) {
    fun validate(): BudgetCreateDto {
        org.valiktor.validate(this) {
            validate(BudgetCreateDto::threshold).isPositive().scaleBetween(0, 2)
            validate(BudgetCreateDto::notes).hasSize(1, 128)
            validate(BudgetCreateDto::name).isNotBlank().hasSize(1, 64)
            validate(BudgetCreateDto::currencyIsoCode).isNotBlank().hasSize(3)
            validate(BudgetCreateDto::fromDate).isNotNull()
            validate(BudgetCreateDto::toDate).isNotNull()
            validate(BudgetCreateDto::accountIds).isNotNull().isNotEmpty()
            validate(BudgetCreateDto::categoryIds).isNotNull().isNotEmpty()
        }
        return this
    }
}