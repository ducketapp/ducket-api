package dev.ducketapp.service.domain.service

import dev.ducketapp.service.domain.mapper.OperationMapper
import dev.ducketapp.service.domain.model.category.Category
import dev.ducketapp.service.domain.model.imports.ImportCreate
import dev.ducketapp.service.domain.model.imports.ImportRule
import dev.ducketapp.service.domain.model.operation.OperationCreate
import dev.ducketapp.service.app.ImportRuleApplyType
import dev.ducketapp.service.app.OperationType
import dev.ducketapp.service.app.database.Transactional
import dev.ducketapp.service.domain.controller.imports.dto.OperationImportDto
import dev.ducketapp.service.domain.controller.imports.dto.ImportDto
import dev.ducketapp.service.domain.controller.imports.dto.ImportUpdateDto
import dev.ducketapp.service.domain.mapper.ImportMapper
import dev.ducketapp.service.domain.repository.*
import dev.ducketapp.service.plugins.DuplicateDataException
import dev.ducketapp.service.plugins.InvalidDataException
import dev.ducketapp.service.plugins.NoDataFoundException
import org.ahocorasick.trie.Trie


class ImportService(
    private val importRepository: ImportRepository,
    private val accountRepository: AccountRepository,
    private val importRuleRepository: ImportRuleRepository,
    private val operationRepository: OperationRepository,
): Transactional {

    suspend fun createImport(userId: Long, accountId: Long, title: String, fileContent: List<OperationImportDto>): ImportDto {
        accountRepository.findOne(userId, accountId) ?: throw NoDataFoundException("No such account was found")
        importRepository.findOneByTitle(userId, title)?.also { throw DuplicateDataException() }

        if (fileContent.map { it.currency }.distinct().size != 1) {
            throw InvalidDataException("Data table should contain records of only one currency")
        }

        val userImportRules = importRuleRepository.findAll(userId)

        return blockingTransaction {
            importRepository.create(ImportCreate(userId, title)).also { import ->
                fileContent.map {
                    OperationMapper.mapDtoToModel(it, userId, accountId, import.id).let { operation ->
                        resolveCategoryByRule(operation, userImportRules)?.let { category ->
                            operation.copy(categoryId = category.id)
                        } ?: operation
                    }
                }.also { operations ->
                    operations.chunked(250).forEach {
                        operationRepository.createBatch(it)
                    }
                }
            }
        }.let {
            ImportMapper.mapModelToDto(it)
        }
    }

    suspend fun updateImport(userId: Long, importId: Long, dto: ImportUpdateDto): ImportDto {
        importRepository.findOneByTitle(userId, dto.title)?.takeIf { it.id != importId }?.also { throw DuplicateDataException() }

        return importRepository.update(userId, importId, ImportMapper.mapDtoToModel(dto))?.let {
            ImportMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getImport(userId: Long, importId: Long): ImportDto {
        return importRepository.findOne(userId, importId)?.let {
            ImportMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getImports(userId: Long): List<ImportDto> {
        return importRepository.findAll(userId).map { ImportMapper.mapModelToDto(it) }
    }

    suspend fun deleteImport(userId: Long, importId: Long) {
        importRepository.delete(userId, importId)
    }

    private fun resolveCategoryByRule(operation: OperationCreate, rules: List<ImportRule>): Category? {
        val text = listOfNotNull(operation.subject, operation.description, operation.notes).joinToString(" ")
        return rules.asSequence()
            .filter {
                when (it.lookupType) {
                    ImportRuleApplyType.EXPENSE_ONLY -> operation.type == OperationType.EXPENSE
                    ImportRuleApplyType.INCOME_ONLY -> operation.type == OperationType.INCOME
                    else -> true
                }
            }.map {
                val trie = Trie.builder().ignoreCase().addKeywords(it.keywords).build()
                val emits = trie.parseText(text)
                Pair(it, emits.size)
            }
            .filter { it.second > 0 }
            .maxByOrNull { it.second }
            ?.first
            ?.category
    }
}