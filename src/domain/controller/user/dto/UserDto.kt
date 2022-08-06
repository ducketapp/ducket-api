package dev.ducket.api.domain.controller.user.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import dev.ducket.api.domain.model.user.User
import dev.ducket.api.utils.toLocalDate
import java.time.Instant
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val currency: CurrencyDto,
    val createdAt: Long,
    val modifiedAt: Long,
)
