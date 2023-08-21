package org.expenny.service.domain.service

import org.expenny.service.domain.controller.BulkDeleteDto
import org.expenny.service.domain.controller.rule.dto.ImportRuleCreateUpdateDto
import org.expenny.service.domain.controller.rule.dto.ImportRuleDto
import org.expenny.service.domain.mapper.ImportRuleMapper
import org.expenny.service.domain.repository.ImportRuleRepository
import org.expenny.service.plugins.DuplicateDataException
import org.expenny.service.plugins.NoDataFoundException

class ImportRuleService(
    private val importRuleRepository: ImportRuleRepository,
) {

    suspend fun createImportRule(userId: Long, dto: ImportRuleCreateUpdateDto): ImportRuleDto {
        importRuleRepository.findOneByTitle(userId, dto.title)?.also { throw DuplicateDataException() }

        return importRuleRepository.create(userId, ImportRuleMapper.mapDtoToModel(dto, userId)).let {
            ImportRuleMapper.mapModelToDto(it)
        }
    }

    suspend fun getImportRules(userId: Long): List<ImportRuleDto> {
        return importRuleRepository.findAll(userId).map { ImportRuleMapper.mapModelToDto(it) }
    }

    suspend fun getImportRule(userId: Long, importRuleId: Long): ImportRuleDto {
        return importRuleRepository.findOne(userId, importRuleId)?.let {
            ImportRuleMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun updateImportRule(userId: Long, importRuleId: Long, dto: ImportRuleCreateUpdateDto): ImportRuleDto {
        importRuleRepository.findOneByTitle(userId, dto.title)?.takeIf { it.id != importRuleId }?.also { throw DuplicateDataException() }

        return importRuleRepository.updateOne(userId, importRuleId, ImportRuleMapper.mapDtoToModel(dto))?.let {
            ImportRuleMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun deleteImportRules(userId: Long, dto: BulkDeleteDto) {
        importRuleRepository.delete(userId, *dto.ids.toLongArray())
    }

    suspend fun deleteImportRule(userId: Long, ruleId: Long) {
        importRuleRepository.delete(userId, ruleId)
    }
}