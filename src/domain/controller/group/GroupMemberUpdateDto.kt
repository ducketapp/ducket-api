package io.ducket.api.domain.controller.group

import org.valiktor.functions.validateForEach

data class GroupMemberUpdateDto(
    val accountPermissions: List<GroupMemberAccountPermissionUpdateDto>,
) {

    fun validate(): GroupMemberUpdateDto {
        org.valiktor.validate(this) {
            validate(GroupMemberUpdateDto::accountPermissions).validateForEach { it.validate() }
        }
        return this
    }
}
