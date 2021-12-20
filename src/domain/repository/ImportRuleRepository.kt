package io.ducket.api.domain.repository

import domain.model.category.CategoryEntity
import domain.model.imports.ImportRule
import domain.model.imports.ImportRuleEntity
import domain.model.imports.ImportRulesTable
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.rule.ImportRuleCreateDto
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class ImportRuleRepository {

    fun create(userId: Long, dto: ImportRuleCreateDto): ImportRule = transaction {
        ImportRuleEntity.new {
            name = dto.name
            recordCategory = CategoryEntity[dto.categoryId]
            user = UserEntity[userId]
            isExpense = dto.expense
            isIncome = dto.income
            keywords = dto.keywords
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findAll(userId: Long): List<ImportRule> = transaction {
        ImportRuleEntity.find { ImportRulesTable.userId.eq(userId) }.toList().map { it.toModel() }
    }

    fun findOneByName(userId: Long, ruleName: String): ImportRule? = transaction {
        ImportRuleEntity.find {
            ImportRulesTable.userId.eq(userId).and(ImportRulesTable.name.eq(ruleName))
        }.firstOrNull()?.toModel()
    }

    fun findOne(userId: Long, ruleId: Long): ImportRule? = transaction {
        ImportRuleEntity.find {
            ImportRulesTable.userId.eq(userId).and(ImportRulesTable.id.eq(ruleId))
        }.firstOrNull()?.toModel()
    }

    fun deleteOne(userId: Long, ruleId: Long): Boolean = transaction {
        ImportRulesTable.deleteWhere {
            ImportRulesTable.userId.eq(userId).and(ImportRulesTable.id.eq(ruleId))
        } > 0
    }
}