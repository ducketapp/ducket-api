package io.ducket.api.domain.controller.group

import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotEmpty

data class GroupCreateDto(
    val name: String,
) {
    fun validate(): GroupCreateDto {
        org.valiktor.validate(this) {
            validate(GroupCreateDto::name).isNotEmpty().hasSize(1, 32)
        }
        return this
    }
}
