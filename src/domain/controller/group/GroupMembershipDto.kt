package io.ducket.api.domain.controller.group

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.app.MembershipStatus
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.group.GroupMembership
import io.ducket.api.utils.InstantSerializer
import java.time.Instant

data class GroupMembershipDto(
    val id: Long,
    val member: UserDto,
    val status: MembershipStatus,
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant,
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant
) {
    constructor(groupMembership: GroupMembership): this(
        id = groupMembership.id,
        member = UserDto(groupMembership.member),
        status = groupMembership.status,
        createdAt = groupMembership.createdAt,
        modifiedAt = groupMembership.modifiedAt,
    )
}
