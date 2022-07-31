package dev.ducket.api.domain.controller.account.dto

import dev.ducket.api.app.AccountType
import dev.ducket.api.app.DEFAULT_SCALE
import dev.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class AccountUpdateDto(
    val name: String,
    val notes: String?,
    val startBalance: BigDecimal,
    val type: AccountType,
) {
    fun validate(): AccountUpdateDto {
        org.valiktor.validate(this) {
            validate(AccountUpdateDto::name).hasSize(1, 64)
            validate(AccountUpdateDto::notes).hasSize(1, 128)
            validate(AccountUpdateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
        }
        return this
    }
}