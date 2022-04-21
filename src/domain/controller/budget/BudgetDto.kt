package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.utils.InstantSerializer
import io.ducket.api.utils.LocalDateSerializer
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.category.TypelessCategoryDto
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.budget.Budget
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetDto(
    val id: Long,
    val name: String,
    val notes: String?,
    val isClosed: Boolean,
    var percentage: BigDecimal,
    var total: BigDecimal,
    val threshold: BigDecimal,
    val owner: UserDto,
    val currency: CurrencyDto,
    val accounts: List<AccountDto>,
    val categories: List<TypelessCategoryDto>,
    @JsonSerialize(using = LocalDateSerializer::class) val fromDate: LocalDate,
    @JsonSerialize(using = LocalDateSerializer::class) val toDate: LocalDate,
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant,
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant,
) {
    constructor(budget: Budget, progress: BudgetProgressDto): this(
        id = budget.id,
        name = budget.name,
        isClosed = budget.isClosed,
        fromDate = budget.fromDate,
        toDate = budget.toDate,
        percentage = progress.percentage,
        total = progress.amount,
        threshold = budget.limit,
        owner = UserDto(budget.user),
        currency = CurrencyDto(budget.currency),
        accounts = budget.accounts.map { AccountDto(it) },
        categories = budget.categories.map { TypelessCategoryDto(it) },
        notes = budget.notes,
        createdAt = budget.createdAt,
        modifiedAt = budget.modifiedAt,
    )
}