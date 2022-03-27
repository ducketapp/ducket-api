package io.ducket.api.domain.controller.group

import io.ducket.api.extension.declaredMemberPropertiesNull
import io.ducket.api.plugins.InvalidDataException
import org.valiktor.functions.*

data class GroupUpdateDto(
    val name: String?,
) {
    fun validate(): GroupUpdateDto {
        org.valiktor.validate(this) {
            validate(GroupUpdateDto::name).isNotEmpty().hasSize(1, 32)

            if (this@GroupUpdateDto.declaredMemberPropertiesNull()) throw InvalidDataException()
        }
        return this
    }
}