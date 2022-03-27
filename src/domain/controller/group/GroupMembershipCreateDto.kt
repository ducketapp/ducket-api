package io.ducket.api.domain.controller.group

import org.valiktor.functions.*

class GroupMembershipCreateDto(
    val memberEmail: String,
) {
    fun validate(): GroupMembershipCreateDto {
        org.valiktor.validate(this) {
            validate(GroupMembershipCreateDto::memberEmail).isNotBlank().isEmail()
        }
        return this
    }
}