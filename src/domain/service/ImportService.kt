package io.ducket.api.domain.service

import domain.model.imports.ImportRule
import io.ducket.api.app.ImportRuleLookupType.*
import io.ducket.api.app.OperationType
import io.ducket.api.app.OperationType.*
import io.ducket.api.domain.controller.imports.CsvRecordDto
import io.ducket.api.domain.controller.imports.ImportDto
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.utils.trimWhitespaces
import org.ahocorasick.trie.Trie
import org.apache.commons.csv.CSVRecord
import java.lang.Exception
import java.time.Instant
import java.time.format.DateTimeParseException


class ImportService(
    private val importRepository: ImportRepository,
    private val accountRepository: AccountRepository,
    private val importRuleRepository: ImportRuleRepository,
    private val categoryRepository: CategoryRepository,
    private val operationRepository: OperationRepository,
): LocalFileService() {
    private val csvRecordsTableColumns = listOf("date", "category", "subject", "description", "notes", "type", "amount", "currency")

    fun getImports(userId: Long): List<ImportDto> {
        return importRepository.getAllByUserId(userId).map { ImportDto(it) }
    }

//    fun importAccountLedgerRecords(userId: Long, accountId: Long, multipartData: List<PartData>): List<LedgerRecordDto> {
//        accountRepository.findOne(userId, accountId) ?: throw NoEntityFoundException()
//
//        val importDataPair = extractImportData(multipartData)
//        val reader = BufferedReader(InputStreamReader(importDataPair.second.inputStream()))
//
//        val csvParser = CSVParser(reader, CSVFormat.DEFAULT
//            .withFirstRecordAsHeader()
//            .withDelimiter(',')
//            .withIgnoreHeaderCase().withTrim()
//        )
//
//        if (!csvParser.headerMap.keys.containsAll(csvRecordsTableColumns) && csvParser.headerMap.size != csvRecordsTableColumns.size) {
//            throw InvalidDataException("Invalid csv columns set")
//        }
//
//        val csvRows = csvParser.records
//
//        if (csvRows.size == 0) {
//            throw InvalidDataException("Csv table can not be empty")
//        }
//
//        val csvRecords = csvRows.map { mapToCsvRecordDto(it) }.also { csvRecords ->
//            val rules = importRuleRepository.findAll(userId)
//            val resolvedCsvRecords = resolveCsvRecordsCategories(csvRecords, rules)
//
//            val groupCategoriesMap = categoryRepository.findAll().groupBy { it.group }
//
//            resolvedCsvRecords.map { csvRecord ->
//                LedgerRecordCreateDto(
//                    amount = csvRecord.amount,
//                    type = csvRecord.type,
//                    accountId = accountId,
//                    operation = OperationCreateDto(
//                        category = csvRecord.category,
//                        categoryGroup = groupCategoriesMap
//                    )
//                )
//            }
//        }
//
//        val importFile = createLocalImportFile(importDataPair.first.extension, importDataPair.second)
//
//        transaction {
//            csvRecords.forEach {
//                val operationId = operationRepository.create(
//                    userId = userId,
//                    dto = OperationCreateDto(
//                        category =
//                    )
//                ).id.value
//
//                ledgerRepository.createTransfer(
//                    operationId = operationId,
//                    transferAccountId = payload.toAccountId,
//                    accountId = payload.fromAccountId,
//                    amount = payload.amount,
//                    rate = rate,
//                )
//            }
//        }
//        val newRecords = importRepository.importLedgerRecords(userId, accountId, csvTransactions, importFile)
//
//        return newRecords.map { TransactionDto(it) }
//    }

    private fun mapToCsvRecordDto(record: CSVRecord): CsvRecordDto {
        try {
            return CsvRecordDto(
                date = Instant.parse(record[csvRecordsTableColumns[0]]),
                category = record[csvRecordsTableColumns[1]].trimWhitespaces().uppercase(),
                subject = record[csvRecordsTableColumns[2]].trimWhitespaces(),
                description = record[csvRecordsTableColumns[3]].trimWhitespaces(),
                notes = record[csvRecordsTableColumns[4]].trimWhitespaces(),
                type = OperationType.valueOf(record[csvRecordsTableColumns[5]].trimWhitespaces()),
                amount = record[csvRecordsTableColumns[6]].toBigDecimal(),
            )
        } catch (e1: DateTimeParseException) {
            throw InvalidDataException("Invalid date at row #${record.recordNumber}: ${record[0]}")
        } catch (e2: IllegalArgumentException) {
            throw InvalidDataException("Invalid type at row #${record.recordNumber}: ${record[5]}")
        } catch (e3: NumberFormatException) {
            throw InvalidDataException("Invalid amount at row #${record.recordNumber}: ${record[6]}")
        } catch (e4: Exception) {
            throw InvalidDataException("Invalid data at row #${record.recordNumber}")
        }
    }

    private fun resolveCsvRecordsCategories(csvRecords: List<CsvRecordDto>, rules: List<ImportRule>): List<CsvRecordDto> {
        val categories = categoryRepository.findAll().map { it.name }

        return csvRecords.map { csvRecord ->
            if (csvRecord.category.isBlank() || !categories.contains(csvRecord.category)) {
                val ruleResolvingText = csvRecord.subject + " " + csvRecord.description

                val rule = rules.filter {
                    if (it.lookupType == EXPENSE_ONLY) return@filter csvRecord.type == EXPENSE
                    if (it.lookupType == INCOME_ONLY) return@filter csvRecord.type == INCOME
                    return@filter true
                }.map { rule ->
                    val trie = Trie.builder().ignoreCase().addKeywords(rule.keywords).build()
                    val emits = trie.parseText(ruleResolvingText)
                    Pair(rule, emits.size)
                }.filter { it.second > 0 }.maxByOrNull { it.second }?.first

                if (rule != null) {
                    csvRecord.category = rule.category.name
                } else {
                    csvRecord.category = "Uncategorized"
                }
            }

            return@map csvRecord
        }
    }
}