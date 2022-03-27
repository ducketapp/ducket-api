package io.ducket.api.domain.controller.account

import io.ducket.api.app.AccountType
import io.ducket.api.plugins.InvalidDataException
import org.valiktor.functions.*
import java.math.BigDecimal

data class AccountCreateDto(
    val name: String,
    val notes: String? = "",
    val startBalance: BigDecimal = BigDecimal.ZERO,
    val currencyIsoCode: String,
    val accountType: AccountType,
) {
    fun validate(): AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::name).isNotNull().hasSize(1, 45)
            validate(AccountCreateDto::notes).hasSize(0, 128)
            validate(AccountCreateDto::startBalance).isNotNull()
            validate(AccountCreateDto::currencyIsoCode).isNotBlank().hasSize(3, 3)
            validate(AccountCreateDto::accountType).isNotNull().isIn(AccountType.values().toList())

            if (startBalance.scale() !in 0..2) {
                throw InvalidDataException("Amount scale should not be greater than 2")
            }
        }
        return this
    }
}