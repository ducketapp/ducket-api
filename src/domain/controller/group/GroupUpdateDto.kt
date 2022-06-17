package io.ducket.api.domain.controller.group

import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank

data class GroupUpdateDto(
    val name: String,
) {
    fun validate(): GroupUpdateDto {
        org.valiktor.validate(this) {
            validate(GroupUpdateDto::name).isNotBlank().hasSize(1, 32)
        }
        return this
    }
}
