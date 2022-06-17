package io.ducket.api.domain.controller.group

import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotEmpty

data class GroupMemberCreateDto(
    val memberEmail: String,
) {
    fun validate(): GroupMemberCreateDto {
        org.valiktor.validate(this) {
            validate(GroupMemberCreateDto::memberEmail).isNotEmpty().isEmail()
        }
        return this
    }
}
