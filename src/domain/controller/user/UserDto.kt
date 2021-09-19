package io.budgery.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.budgery.api.InstantSerializer
import io.budgery.api.domain.controller.account.CurrencyDto
import domain.model.user.User
import io.budgery.api.domain.controller.record.AttachmentDto
import java.time.Instant
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(@JsonIgnore val user: User) {
    val id: Int = user.id
    val name: String = user.name
    val email: String = user.email
    val mainCurrency: CurrencyDto = CurrencyDto(user.mainCurrency)
    val attachment: AttachmentDto? = user.attachment?.let { AttachmentDto(user.attachment) }
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = user.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = user.modifiedAt
}
