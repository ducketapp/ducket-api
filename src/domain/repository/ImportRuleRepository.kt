package org.expenny.service.domain.repository

import org.expenny.service.domain.model.category.CategoryEntity
import org.expenny.service.domain.model.imports.*
import org.expenny.service.domain.model.imports.ImportRulesTable
import org.expenny.service.domain.model.user.UserEntity
import org.expenny.service.app.database.Transactional
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class ImportRuleRepository: Transactional {

    suspend fun create(userId: Long, data: ImportRuleCreate): ImportRule = blockingTransaction {
        ImportRuleEntity.new {
            this.title = data.title
            this.category = CategoryEntity[data.categoryId]
            this.user = UserEntity[userId]
            this.lookupType = data.lookupType
            this.keywords = data.keywords.joinToString(KEYWORDS_DELIMITER)
        }.toModel()
    }

    suspend fun updateOne(userId: Long, importRuleId: Long, data: ImportRuleUpdate): ImportRule? = blockingTransaction {
        ImportRuleEntity.find {
            ImportRulesTable.id.eq(importRuleId).and(ImportRulesTable.userId.eq(userId))
        }.firstOrNull()?.apply {
            this.title = data.title
            this.category = CategoryEntity[data.categoryId]
            this.user = UserEntity[userId]
            this.lookupType = data.lookupType
            this.keywords = data.keywords.joinToString(KEYWORDS_DELIMITER)
        }?.toModel()
    }

    suspend fun findAll(userId: Long): List<ImportRule> = blockingTransaction {
        ImportRuleEntity.find { ImportRulesTable.userId.eq(userId) }.toList().map { it.toModel() }
    }

    suspend fun findOneByTitle(userId: Long, title: String): ImportRule? = blockingTransaction {
        ImportRuleEntity.find {
            ImportRulesTable.userId.eq(userId).and(ImportRulesTable.title.eq(title))
        }.firstOrNull()?.toModel()
    }

    suspend fun findOne(userId: Long, importRuleId: Long): ImportRule? = blockingTransaction {
        ImportRuleEntity.find {
            ImportRulesTable.userId.eq(userId).and(ImportRulesTable.id.eq(importRuleId))
        }.firstOrNull()?.toModel()
    }

    suspend fun delete(userId: Long, vararg importRuleIds: Long): Unit = blockingTransaction {
        ImportRulesTable.deleteWhere {
            ImportRulesTable.id.inList(importRuleIds.asList()).and(ImportRulesTable.userId.eq(userId))
        }
    }
}