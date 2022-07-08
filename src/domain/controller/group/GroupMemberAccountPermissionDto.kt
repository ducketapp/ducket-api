package io.ducket.api.domain.controller.group

import io.ducket.api.app.AccountPermission
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.model.group.GroupMemberAccountPermission

data class GroupMemberAccountPermissionDto(
    val id: Long,
    val account: AccountDto,
    val accountPermission: AccountPermission,
) {
    constructor(groupMemberPermission: GroupMemberAccountPermission): this(
        id = groupMemberPermission.id,
        account = AccountDto(groupMemberPermission.account),
        accountPermission = groupMemberPermission.accountPermission,
    )
}
