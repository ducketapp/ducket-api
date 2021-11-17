package io.ducket.api.domain.repository

import domain.model.rule.Rule
import domain.model.rule.RuleEntity
import domain.model.rule.RulesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class RuleRepository {

    fun findAll(userId: String): List<Rule> = transaction {
        RuleEntity.find { RulesTable.userId.eq(userId) }.toList().map { it.toModel() }
    }

    fun findOne(userId: String, ruleId: String): Rule? = transaction {
        RuleEntity.find {
            RulesTable.userId.eq(userId).and(RulesTable.id.eq(ruleId))
        }.firstOrNull()?.toModel()
    }

    fun deleteOne(userId: String, ruleId: String): Boolean = transaction {
        RulesTable.deleteWhere {
            RulesTable.userId.eq(userId).and(RulesTable.id.eq(ruleId))
        } > 0
    }
}