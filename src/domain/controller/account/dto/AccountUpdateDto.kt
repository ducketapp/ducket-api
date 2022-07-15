package io.ducket.api.domain.controller.account.dto

import io.ducket.api.app.AccountType
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class AccountUpdateDto(
    val title: String,
    val notes: String?,
    val startBalance: BigDecimal,
    val type: AccountType,
) {
    fun validate(): AccountUpdateDto {
        org.valiktor.validate(this) {
            validate(AccountUpdateDto::title).hasSize(1, 64)
            validate(AccountUpdateDto::notes).hasSize(1, 128)
            validate(AccountUpdateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
        }
        return this
    }
}