package org.expenny.service.domain.controller

import org.valiktor.functions.*

data class BulkDeleteDto(
    val ids: List<Long>,
) {
    fun validate(): BulkDeleteDto {
        org.valiktor.validate(this) {
            validate(BulkDeleteDto::ids).isNotNull().isNotEmpty()
        }
        return this
    }
}
