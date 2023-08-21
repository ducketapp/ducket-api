package org.expenny.service.domain.controller.account.dto

import org.expenny.service.app.AccountType
import org.expenny.service.app.DEFAULT_SCALE
import org.expenny.service.utils.hasLength
import org.expenny.service.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class AccountCreateDto(
    val name: String,
    val startBalance: BigDecimal,
    val currency: String,
    val type: AccountType,
    val notes: String?,
) {
    fun validate(): AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::name).isNotNull().hasSize(1, 64)
            validate(AccountCreateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
            validate(AccountCreateDto::currency).isNotBlank().hasLength(3)
            validate(AccountCreateDto::notes).hasSize(1, 128)
        }
        return this
    }
}