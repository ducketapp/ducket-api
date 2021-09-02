package io.budgery.api.domain.controller.label

import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotNull

data class LabelCreateDto(val name: String) {
    fun validate() : LabelCreateDto {
        org.valiktor.validate(this) {
            validate(LabelCreateDto::name).isNotNull().hasSize(1, 45)
        }
        return this
    }
}