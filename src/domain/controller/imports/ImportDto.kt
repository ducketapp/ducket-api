package io.ducket.api.domain.controller.imports

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import domain.model.imports.Import
import java.time.Instant

data class ImportDto(@JsonIgnore val import: Import) {
    val id: String = import.id.toString()
    val fileName: String = import.filePath.substringAfterLast("\\")
    @JsonSerialize(using = InstantSerializer::class) val importedAt: Instant = import.importedAt
}