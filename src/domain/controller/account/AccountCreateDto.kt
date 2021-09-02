package io.budgery.api.domain.controller.account

import org.valiktor.functions.*

data class AccountCreateDto(val name: String, val notes: String? = "", val currencyId: Int, val accountTypeId: Int) {
    fun validate() : AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::name).isNotNull().hasSize(1, 45)
            validate(AccountCreateDto::notes).hasSize(0, 128)
            validate(AccountCreateDto::currencyId).isNotZero().isPositive()
            validate(AccountCreateDto::accountTypeId).isNotZero().isPositive()
        }
        return this
    }
}