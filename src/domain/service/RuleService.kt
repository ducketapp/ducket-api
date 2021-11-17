package io.ducket.api.domain.service

import io.ducket.api.NoEntityFoundError
import io.ducket.api.domain.controller.transaction.RuleDto
import io.ducket.api.domain.repository.RuleRepository

class RuleService(
    private val ruleRepository: RuleRepository,
) {

    fun getRules(userId: String): List<RuleDto> {
        return ruleRepository.findAll(userId).map { RuleDto(it) }
    }

    fun getRule(userId: String, ruleId: String): RuleDto {
        return ruleRepository.findOne(userId, ruleId)?.let { RuleDto(it) }
            ?: throw NoEntityFoundError("No such rule was found")
    }

    fun deleteRule(userId: String, ruleId: String): Boolean {
        return ruleRepository.deleteOne(userId, ruleId)
    }
}