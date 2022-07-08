package io.ducket.api.domain.controller.account.dto

import io.ducket.api.app.AccountType
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class AccountCreateDto(
    val name: String,
    val startBalance: BigDecimal = BigDecimal.ZERO,
    val currency: String,
    val type: AccountType,
    val notes: String? = null,
) {
    fun validate(): AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::name).isNotNull().hasSize(1, 64)
            validate(AccountCreateDto::notes).hasSize(1, 128)
            validate(AccountCreateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
            validate(AccountCreateDto::currency).isNotBlank().hasSize(3, 3)
            validate(AccountCreateDto::type).isNotNull()
        }
        return this
    }
}