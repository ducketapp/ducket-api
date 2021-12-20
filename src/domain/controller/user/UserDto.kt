package io.ducket.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.currency.CurrencyDto
import domain.model.user.User
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(@JsonIgnore val user: User) {
    val id: Long = user.id
    val phone: String? = user.phone
    val name: String = user.name
    val email: String = user.email
    val mainCurrency: CurrencyDto = CurrencyDto(user.mainCurrency)
    // val images: List<AttachmentDto> = user.images.map { AttachmentDto(it) }
}
