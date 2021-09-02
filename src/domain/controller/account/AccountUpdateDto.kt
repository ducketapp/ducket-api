package io.budgery.api.domain.controller.account

import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotZero
import org.valiktor.functions.isPositive
import java.lang.IllegalArgumentException

data class AccountUpdateDto(val name: String?, val notes: String?, val accountTypeId: Int?) {
    fun validate() : AccountUpdateDto {
        org.valiktor.validate(this) {
            validate(AccountUpdateDto::name).hasSize(1, 45)
            validate(AccountUpdateDto::notes).hasSize(0, 128)
            validate(AccountUpdateDto::accountTypeId).isNotZero().isPositive()

            if (name == null && notes == null && accountTypeId == null) {
                throw IllegalArgumentException("No fields specified for updating")
            }
        }
        return this
    }
}