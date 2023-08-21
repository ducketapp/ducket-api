package org.expenny.service.domain.controller.imports.dto

import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank

data class ImportUpdateDto(
    val title: String,
) {
    fun validate(): ImportUpdateDto {
        org.valiktor.validate(this) {
            validate(ImportUpdateDto::title).isNotBlank().hasSize(1, 64)
        }
        return this
    }
}
