package io.ducket.api.domain.controller.account

import io.ducket.api.app.AccountType
import io.ducket.api.app.LedgerRecordType
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class AccountCreateDto(
    val name: String,
    val startBalance: BigDecimal = BigDecimal.ZERO,
    val currencyIsoCode: String,
    val accountType: AccountType,
    val notes: String? = null,
) {
    fun validate(): AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::name).isNotNull().hasSize(1, 64)
            validate(AccountCreateDto::notes).hasSize(0, 128)
            validate(AccountCreateDto::startBalance).scaleBetween(0, 2)
            validate(AccountCreateDto::currencyIsoCode).isNotBlank().hasSize(3, 3)
            validate(AccountCreateDto::accountType).isNotNull()
        }
        return this
    }
}