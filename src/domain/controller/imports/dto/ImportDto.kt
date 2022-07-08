package io.ducket.api.domain.controller.imports.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.utils.InstantSerializer
import domain.model.imports.Import
import java.time.Instant

data class ImportDto(
    val id: Long,
    val title: String,
    @JsonSerialize(using = InstantSerializer::class) val importedAt: Instant,
)
