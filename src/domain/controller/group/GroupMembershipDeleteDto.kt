package io.ducket.api.domain.controller.group

import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull

data class GroupMembershipDeleteDto(
    val membershipIds: List<Long>,
) {
    fun validate(): GroupMembershipDeleteDto {
        org.valiktor.validate(this) {
            validate(GroupMembershipDeleteDto::membershipIds).isNotNull().isNotEmpty()
        }
        return this
    }
}