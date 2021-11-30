package io.ducket.api.domain.controller.follow

import org.valiktor.functions.isNotEmpty

class FollowUserDto(val userId: String) {
    fun validate() : FollowUserDto {
        org.valiktor.validate(this) {
            validate(FollowUserDto::userId).isNotEmpty()
        }
        return this
    }
}