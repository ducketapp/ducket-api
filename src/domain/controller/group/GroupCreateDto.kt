package io.ducket.api.domain.controller.group

import org.valiktor.functions.*

data class GroupCreateDto(
    val name: String,
    val members: List<GroupMembershipCreateDto>,
) {
    fun validate(): GroupCreateDto {
        org.valiktor.validate(this) {
            validate(GroupCreateDto::name).isNotBlank().hasSize(1, 64)
            members.forEach { it.validate() }
        }
        return this
    }
}