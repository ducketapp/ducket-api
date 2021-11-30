package io.ducket.api.domain.controller.follow

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.follow.Follow
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
class FollowingDto(@JsonIgnore val follow: Follow) {
    val id: String = follow.id
    val followedUser: UserDto = UserDto(follow.followed)
    val isPendingApproval: Boolean = follow.isPending
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = follow.createdAt
}