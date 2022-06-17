package io.ducket.api.domain.controller.tag

import org.valiktor.functions.hasSize

data class TagCreateDto(
    val name: String
) {
    fun validate(): TagCreateDto {
        org.valiktor.validate(this) {
            validate(TagCreateDto::name).hasSize(1, 32)
        }
        return this
    }
}
