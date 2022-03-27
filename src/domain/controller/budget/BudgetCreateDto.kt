package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.LocalDateDeserializer
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate


data class BudgetCreateDto(
    val thresholdAmount: BigDecimal,
    val name: String,
    val currencyIsoCode: String,
    val accountIds: List<Long>,
    val categoryIds: List<Long>,
    @JsonDeserialize(using = LocalDateDeserializer::class) val fromDate: LocalDate,
    @JsonDeserialize(using = LocalDateDeserializer::class) val toDate: LocalDate,
) {
    fun validate(): BudgetCreateDto {
        org.valiktor.validate(this) {
            validate(BudgetCreateDto::thresholdAmount).isGreaterThan(BigDecimal.ZERO)
            validate(BudgetCreateDto::name).isNotBlank().hasSize(1, 45)
            validate(BudgetCreateDto::currencyIsoCode).isNotBlank().hasSize(3)
            validate(BudgetCreateDto::fromDate).isNotNull()
            validate(BudgetCreateDto::toDate).isNotNull()
            validate(BudgetCreateDto::accountIds).isNotNull().isNotEmpty()
            validate(BudgetCreateDto::categoryIds).isNotNull().isNotEmpty()
        }
        return this
    }
}