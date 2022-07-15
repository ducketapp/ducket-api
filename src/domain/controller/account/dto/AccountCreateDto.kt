package io.ducket.api.domain.controller.account.dto

import io.ducket.api.app.AccountType
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.utils.hasLength
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class AccountCreateDto(
    val title: String,
    val startBalance: BigDecimal,
    val currency: String,
    val type: AccountType,
    val notes: String?,
) {
    fun validate(): AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::title).isNotNull().hasSize(1, 64)
            validate(AccountCreateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
            validate(AccountCreateDto::currency).isNotBlank().hasLength(3)
            validate(AccountCreateDto::notes).hasSize(1, 128)
        }
        return this
    }
}