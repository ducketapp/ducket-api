package io.ducket.api.domain.controller.group

import io.ducket.api.app.MembershipAction
import org.valiktor.functions.isNotNull
import org.valiktor.functions.isNotZero
import org.valiktor.functions.isPositive

data class GroupMembershipActionDto(
    val action: MembershipAction,
) {
    fun validate(): GroupMembershipActionDto {
        org.valiktor.validate(this) {
            validate(GroupMembershipActionDto::action).isNotNull()
        }
        return this
    }
}