package io.ducket.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.domain.controller.currency.CurrencyDto
import domain.model.user.User
import io.ducket.api.utils.InstantSerializer
import io.ducket.api.utils.LocalDateSerializer
import io.ducket.api.utils.toLocalDate
import java.time.Instant
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    val id: Long,
    val phone: String?,
    val name: String,
    val email: String,
    val mainCurrency: CurrencyDto,
    @JsonSerialize(using = LocalDateSerializer::class) val sinceDate: LocalDate
) {
    constructor(user: User): this(
        id = user.id,
        phone = user.phone,
        name = user.name,
        email = user.email,
        mainCurrency = CurrencyDto(user.mainCurrency),
        sinceDate = user.createdAt.toLocalDate(),
    )
}
