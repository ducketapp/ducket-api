package io.ducket.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonInclude
import io.ducket.api.domain.controller.currency.CurrencyDto
import domain.model.user.User
import io.ducket.api.app.MembershipStatus
import io.ducket.api.domain.controller.group.GroupMembershipDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    val id: Long,
    val phone: String?,
    val name: String,
    val email: String,
    val mainCurrency: CurrencyDto,
) {
    constructor(user: User): this(
        id = user.id,
        phone = user.phone,
        name = user.name,
        email = user.email,
        mainCurrency = CurrencyDto(user.mainCurrency),
    )
}
