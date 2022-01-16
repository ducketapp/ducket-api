package io.ducket.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.ducket.api.domain.controller.currency.CurrencyDto
import domain.model.user.User

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(@JsonIgnore val user: User) {
    val id: Long = user.id
    val phone: String? = user.phone
    val name: String = user.name
    val email: String = user.email
    val mainCurrency: CurrencyDto = CurrencyDto(user.mainCurrency)
}
