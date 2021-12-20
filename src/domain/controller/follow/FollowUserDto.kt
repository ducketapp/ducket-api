package io.ducket.api.domain.controller.follow

import org.valiktor.functions.isNotZero
import org.valiktor.functions.isPositive

class FollowUserDto(val userId: Long) {
    fun validate() : FollowUserDto {
        org.valiktor.validate(this) {
            validate(FollowUserDto::userId).isNotZero().isPositive()
        }
        return this
    }
}