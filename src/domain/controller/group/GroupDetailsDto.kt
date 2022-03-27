package io.ducket.api.domain.controller.group

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.group.Group
import io.ducket.api.domain.model.group.GroupMembership
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GroupDetailsDto(
    val id: Long,
    val name: String,
    val creator: UserDto,
    val membership: GroupMembershipDto,
    val otherMemberships: List<GroupMembershipDto>,
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant,
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant,
) {
    constructor(
        group: Group,
        membership: GroupMembership,
        otherMemberships: List<GroupMembership>,
    ): this(
        id = group.id,
        name = group.name,
        creator = UserDto(group.creator),
        membership = GroupMembershipDto(membership),
        otherMemberships = otherMemberships.map { GroupMembershipDto(it) },
        createdAt = group.createdAt,
        modifiedAt = group.modifiedAt,
    )
}