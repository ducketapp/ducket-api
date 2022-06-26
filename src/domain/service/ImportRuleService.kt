package io.ducket.api.domain.service

import io.ducket.api.domain.controller.rule.ImportRuleCreateDto
import io.ducket.api.domain.controller.rule.ImportRuleDto
import io.ducket.api.domain.controller.rule.ImportRuleUpdateDto
import io.ducket.api.domain.repository.ImportRuleRepository
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException

class ImportRuleService(
    private val importRuleRepository: ImportRuleRepository,
) {

    fun createImportRule(userId: Long, payload: ImportRuleCreateDto): ImportRuleDto {
        importRuleRepository.findOneByName(userId, payload.name)?.let { throw DuplicateDataException() }

        return ImportRuleDto(importRuleRepository.create(userId, payload))
    }

    fun getImportRules(userId: Long): List<ImportRuleDto> {
        return importRuleRepository.findAll(userId).map { ImportRuleDto(it) }
    }

    fun getImportRule(userId: Long, ruleId: Long): ImportRuleDto {
        return importRuleRepository.findOne(userId, ruleId)?.let { ImportRuleDto(it) } ?: throw NoDataFoundException()
    }

    fun updateImportRule(userId: Long, ruleId: Long, payload: ImportRuleUpdateDto): ImportRuleDto {
        payload.name?.also {
            importRuleRepository.findOneByName(userId, it)?.let { found ->
                if (found.id == ruleId) throw DuplicateDataException()
            }
        }

        val updatedImportRule = importRuleRepository.updateOne(userId, ruleId, payload) ?: throw NoDataFoundException()
        return ImportRuleDto(updatedImportRule)
    }

    fun deleteImportRule(userId: Long, ruleId: Long) {
        importRuleRepository.delete(userId, ruleId)
    }
}