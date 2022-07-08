package io.ducket.api.domain.controller.budget.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.utils.LocalDateDeserializer
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate

data class BudgetCreateDto(
    val title: String,
    val limit: BigDecimal,
    val notes: String?,
    val currency: String,
    val categoryId: Long,
    val accountIds: List<Long>,
    @JsonDeserialize(using = LocalDateDeserializer::class) val startDate: LocalDate,
    @JsonDeserialize(using = LocalDateDeserializer::class) val endDate: LocalDate,
) {
    fun validate(): BudgetCreateDto {
        org.valiktor.validate(this) {
            validate(BudgetCreateDto::limit).isNotZero().isPositive().scaleBetween(0, DEFAULT_SCALE)
            validate(BudgetCreateDto::notes).hasSize(1, 128)
            validate(BudgetCreateDto::title).isNotBlank().hasSize(1, 32)
            validate(BudgetCreateDto::currency).isNotBlank().hasSize(3)
            validate(BudgetCreateDto::accountIds).isNotEmpty()
            validate(BudgetCreateDto::categoryId).isPositive()
            validate(BudgetCreateDto::startDate).isLessThanOrEqualTo(endDate)
        }
        return this
    }
}