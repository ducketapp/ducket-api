package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.app.BudgetPeriodType
import io.ducket.api.utils.LocalDateDeserializer
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate


data class BudgetCreateDto(
    val title: String,
    val defaultLimit: BigDecimal,
    val currentPeriod: String,
    val periodType: BudgetPeriodType? = null,
    val notes: String?,
    val currencyIsoCode: String,
    val categoryId: Long,
    val accountIds: List<Long>,
    @JsonDeserialize(using = LocalDateDeserializer::class) val startDate: LocalDate,
    @JsonDeserialize(using = LocalDateDeserializer::class) val endDate: LocalDate?,
) {
    fun validate(): BudgetCreateDto {
        org.valiktor.validate(this) {
            if (periodType != null) {
                validate(BudgetCreateDto::endDate).isNull()
            } else {
                validate(BudgetCreateDto::endDate).isNotNull().isGreaterThanOrEqualTo(startDate)
                validate(BudgetCreateDto::startDate).isLessThanOrEqualTo(endDate!!)
            }
            validate(BudgetCreateDto::currentPeriod).isNotBlank().hasSize(1, 16)
            validate(BudgetCreateDto::defaultLimit).isNotZero().isPositive().scaleBetween(0, 2)
            validate(BudgetCreateDto::notes).hasSize(1, 128)
            validate(BudgetCreateDto::title).isNotBlank().hasSize(1, 32)
            validate(BudgetCreateDto::currencyIsoCode).isNotBlank().hasSize(3)
            validate(BudgetCreateDto::accountIds).isNotEmpty()
            validate(BudgetCreateDto::categoryId).isPositive()
        }
        return this
    }
}