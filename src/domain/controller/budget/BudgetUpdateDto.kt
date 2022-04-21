package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.utils.LocalDateDeserializer
import io.ducket.api.utils.declaredMemberPropertiesNull
import io.ducket.api.plugins.InvalidDataException
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate

data class BudgetUpdateDto(
    val threshold: BigDecimal?,
    val name: String?,
    val notes: String?,
    val accountIds: List<Long>?,
    val categoryIds: List<Long>?,
    @JsonDeserialize(using = LocalDateDeserializer::class) val fromDate: LocalDate?,
    @JsonDeserialize(using = LocalDateDeserializer::class) val toDate: LocalDate?,
) {
    fun validate(): BudgetUpdateDto {
        org.valiktor.validate(this) {
            validate(BudgetUpdateDto::threshold).isPositive()
            validate(BudgetUpdateDto::name).isNotNull().hasSize(1, 64)
            validate(BudgetUpdateDto::notes).isNotNull().hasSize(1, 128)
            validate(BudgetUpdateDto::fromDate).isNotNull()
            validate(BudgetUpdateDto::toDate).isNotNull()
            validate(BudgetUpdateDto::accountIds).isNotNull().isNotEmpty()
            validate(BudgetUpdateDto::categoryIds).isNotNull().isNotEmpty()

            if (this@BudgetUpdateDto.declaredMemberPropertiesNull()) throw InvalidDataException()
        }
        return this
    }
}