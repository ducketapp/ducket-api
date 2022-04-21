package io.ducket.api.domain.controller.imports

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.utils.InstantSerializer
import domain.model.imports.Import
import java.time.Instant

data class ImportDto(
    val id: Long,
    val fileName: String,
    @JsonSerialize(using = InstantSerializer::class) val importedAt: Instant,
) {
    constructor(import: Import): this(
        id = import.id,
        fileName = import.filePath.substringAfterLast("\\"),
        importedAt = import.importedAt
    )
}