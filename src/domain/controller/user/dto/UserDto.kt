package dev.ducket.api.domain.controller.user.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import dev.ducket.api.domain.model.user.User
import dev.ducket.api.utils.toLocalDate
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    val id: Long,
    val phone: String?,
    val name: String,
    val email: String,
    val currency: CurrencyDto,
    val sinceDate: LocalDate
) {
    constructor(user: User): this(
        id = user.id,
        phone = user.phone,
        name = user.name,
        email = user.email,
        currency = CurrencyDto(user.currency),
        sinceDate = user.createdAt.toLocalDate(),
    )
}
