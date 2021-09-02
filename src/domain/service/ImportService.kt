package io.budgery.api.domain.service

import io.budgery.api.domain.controller.imports.ImportDto
import io.budgery.api.domain.repository.ImportRepository

class ImportService(private val importRepository: ImportRepository) {

    fun getImports(userId: Int) : List<ImportDto> {
        return importRepository.getAllByUserId(userId).map { ImportDto(it) }
    }
}