package io.ducket.api.domain.controller.group

import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.group.Group

data class GroupDto(
    val id: Long,
    val name: String,
    val owner: UserDto,
    val memberships: List<GroupMembershipDto>,
) {
    constructor(group: Group): this(
        id = group.id,
        name = group.name,
        owner = UserDto(group.owner),
        memberships = group.memberships.map { GroupMembershipDto(it) },
    )
}
