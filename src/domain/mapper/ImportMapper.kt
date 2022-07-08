package io.ducket.api.domain.mapper

import domain.mapper.DataClassMapper
import domain.model.imports.Import
import domain.model.imports.ImportUpdate
import io.ducket.api.domain.controller.imports.dto.ImportDto
import io.ducket.api.domain.controller.imports.dto.ImportUpdateDto

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