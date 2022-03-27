package io.ducket.api.domain.controller.group

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import io.ducket.api.app.MembershipStatus
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.group.GroupMembership
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
class GroupMembershipDto(
    @JsonIgnore private val groupMembership: GroupMembership
) {
    val id: Long = groupMembership.id
    val member: UserDto = UserDto(groupMembership.member)
    val status: MembershipStatus = groupMembership.status
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = groupMembership.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = groupMembership.modifiedAt
}