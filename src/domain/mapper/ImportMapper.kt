package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.imports.Import
import dev.ducket.api.domain.model.imports.ImportUpdate
import dev.ducket.api.domain.controller.imports.dto.ImportDto
import dev.ducket.api.domain.controller.imports.dto.ImportUpdateDto

object ImportMapper {

    fun mapDtoToModel(dto: ImportUpdateDto): ImportUpdate {
        return DataClassMapper<ImportUpdateDto, ImportUpdate>().invoke(dto)
    }

    fun mapModelToDto(model: Import): ImportDto {
        return DataClassMapper<Import, ImportDto>()
            .map("createdAt", ImportDto::importedAt)
            .invoke(model)
    }
}