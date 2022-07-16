package dev.ducket.api.domain.controller.imports.dto

import java.time.Instant

data class ImportDto(
    val id: Long,
    val title: String,
    val importedAt: Instant,
)
