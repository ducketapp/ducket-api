package io.ducket.api.domain.controller.tag

import org.valiktor.functions.hasSize

data class TagUpdateDto(
    val name: String
) {
    fun validate(): TagUpdateDto {
        org.valiktor.validate(this) {
            validate(TagUpdateDto::name).hasSize(1, 32)
        }
        return this
    }
}