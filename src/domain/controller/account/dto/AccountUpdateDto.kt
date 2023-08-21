package org.expenny.service.domain.controller.account.dto

import org.expenny.service.app.AccountType
import org.expenny.service.app.DEFAULT_SCALE
import org.expenny.service.utils.scaleBetween
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