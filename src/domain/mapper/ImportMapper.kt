package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.imports.Import
import org.expenny.service.domain.model.imports.ImportUpdate
import org.expenny.service.domain.controller.imports.dto.ImportDto
import org.expenny.service.domain.controller.imports.dto.ImportUpdateDto

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