package io.budgery.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.budgery.api.InstantSerializer
import io.budgery.api.domain.controller.account.CurrencyDto
import domain.model.user.User
import java.time.Instant
import java.util.*

data class UserDto(@JsonIgnore val user: User) {
    val id: Int = user.id
    val name: String = user.name
    val email: String = user.email
    val mainCurrency: CurrencyDto = CurrencyDto(user.mainCurrency)
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = user.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = user.modifiedAt
}
