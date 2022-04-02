package io.ducket.api.domain.repository

import domain.model.category.CategoryEntity
import domain.model.imports.*
import domain.model.imports.ImportRulesTable
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.rule.ImportRuleCreateDto
import io.ducket.api.domain.controller.rule.ImportRuleUpdateDto
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
            expense = dto.expense
            income = dto.income
            keywords = dto.keywords.joinToString(KEYWORDS_DELIMITER)
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

    fun updateOne(userId: Long, ruleId: Long, dto: ImportRuleUpdateDto): ImportRule? = transaction {
        ImportRuleEntity.find {
            ImportRulesTable.id.eq(ruleId).and(ImportRulesTable.userId.eq(userId))
        }.firstOrNull()?.also { found ->
            dto.name?.let { found.name = it }
            dto.expense?.let { found.expense = it }
            dto.income?.let { found.income = it }
            dto.keywords?.let { found.keywords = it.joinToString(KEYWORDS_DELIMITER) }
            dto.categoryId?.let { found.recordCategory = CategoryEntity[it] }
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun delete(userId: Long, vararg ruleIds: Long) = transaction {
        ImportRulesTable.deleteWhere {
            ImportRulesTable.id.inList(ruleIds.asList()).and(ImportRulesTable.userId.eq(userId))
        }
    }

    fun deleteAll(userId: Long) = transaction {
        ImportRulesTable.deleteWhere {
            ImportRulesTable.userId.eq(userId)
        }
    }
}