package dev.ducketapp.service.domain.controller.user.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.model.user.User
import dev.ducketapp.service.utils.toLocalDate
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
