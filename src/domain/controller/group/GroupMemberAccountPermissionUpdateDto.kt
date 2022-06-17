package io.ducket.api.domain.controller.group

import io.ducket.api.app.AccountPermission
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isNotNull

data class GroupMemberAccountPermissionUpdateDto(
    val permissionId: Long,
    val accountPermission: AccountPermission,
) {
    fun validate(): GroupMemberAccountPermissionUpdateDto {
        org.valiktor.validate(this) {
            validate(GroupMemberAccountPermissionUpdateDto::permissionId).isGreaterThan(0L)
            validate(GroupMemberAccountPermissionUpdateDto::accountPermission).isNotNull()
        }
        return this
    }
}
