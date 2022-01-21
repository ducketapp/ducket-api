package io.ducket.api.domain.service

import io.ducket.api.domain.controller.rule.ImportRuleCreateDto
import io.ducket.api.domain.controller.rule.ImportRuleDto
import io.ducket.api.domain.repository.ImportRuleRepository
import io.ducket.api.plugins.DuplicateEntityException
import io.ducket.api.plugins.NoEntityFoundException

class ImportRuleService(
    private val importRuleRepository: ImportRuleRepository,
) {

    fun createRule(userId: Long, reqObj: ImportRuleCreateDto): ImportRuleDto {
        importRuleRepository.findOneByName(userId, reqObj.name)?.let {
            throw DuplicateEntityException("'${reqObj.name}' rule already exists")
        }

        return ImportRuleDto(importRuleRepository.create(userId, reqObj))
    }

    fun getRules(userId: Long): List<ImportRuleDto> {
        return importRuleRepository.findAll(userId).map { ImportRuleDto(it) }
    }

    fun getRule(userId: Long, ruleId: Long): ImportRuleDto {
        return importRuleRepository.findOne(userId, ruleId)?.let { ImportRuleDto(it) }
            ?: throw NoEntityFoundException("No such rule was found")
    }

    fun deleteRule(userId: Long, ruleId: Long): Boolean {
        return importRuleRepository.delete(userId, ruleId)
    }
}