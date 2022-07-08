package io.ducket.api.domain.controller.tag.dto

import org.valiktor.functions.hasSize

data class TagCreateUpdateDto(
    val title: String
) {
    fun validate(): TagCreateUpdateDto {
        org.valiktor.validate(this) {
            validate(TagCreateUpdateDto::title).hasSize(1, 32)
        }
        return this
    }
}
