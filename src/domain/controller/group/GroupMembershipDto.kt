package io.ducket.api.domain.controller.group

import io.ducket.api.domain.model.group.GroupMembership

data class GroupMembershipDto(
    val id: Long,
    val memberEmail: String,
    val accountsPermissions: List<GroupMemberAccountPermissionDto>,
) {
    constructor(groupMembership: GroupMembership): this(
        id = groupMembership.id,
        memberEmail = groupMembership.memberEmail,
        accountsPermissions = groupMembership.accountsPermissions.map { GroupMemberAccountPermissionDto(it) },
    )
}
