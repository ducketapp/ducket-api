package io.ducket.api.domain.service

import io.ducket.api.domain.controller.imports.CsvTransactionDto
import io.ducket.api.domain.controller.imports.ImportDto
import io.ducket.api.domain.controller.transaction.TransactionDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.CategoryRepository
import io.ducket.api.domain.repository.ImportRepository
import io.ducket.api.domain.repository.ImportRuleRepository
import io.ducket.api.extension.trimWhitespaces
import io.ducket.api.getLogger
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import io.ktor.http.content.*
import org.ahocorasick.trie.Trie
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant
import kotlin.streams.toList

class ImportService(
    private val importRepository: ImportRepository,
    private val accountRepository: AccountRepository,
    private val importRuleRepository: ImportRuleRepository,
    private val categoryRepository: CategoryRepository,
): FileService() {
    private val logger = getLogger()

    fun getImports(userId: Long): List<ImportDto> {
        return importRepository.getAllByUserId(userId).map { ImportDto(it) }
    }

    fun importAccountTransactions(userId: Long, accountId: Long, multipartData: List<PartData>): List<TransactionDto> {
        accountRepository.findOne(userId, accountId) ?: throw NoEntityFoundException("No such account was found")

        val importDataPair = extractImportData(multipartData)
        val reader = BufferedReader(InputStreamReader(importDataPair.second.inputStream()))

        val csvParser = CSVParser(reader, CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(',')
            .withIgnoreHeaderCase().withTrim())

        val headers = listOf("date", "category", "beneficiary_or_sender", "description", "amount")
        csvParser.headerMap.takeIf { it.keys.containsAll(headers) && it.size == headers.size }
            ?: throw InvalidDataException("Invalid header set")

        val csvRecords = csvParser.records.takeIf { it.size > 0 }
            ?: throw InvalidDataException("Invalid records amount: 0")

        var csvTransactions = csvRecords.map {
            try {
                val date = Instant.parse(it.get(headers[0]))
                val category = it.get(headers[1]).trimWhitespaces()
                val beneficiaryOrSender = it.get(headers[2]).trimWhitespaces()
                val description = it.get(headers[3]).trimWhitespaces()
                val amount = it.get(headers[4]).toBigDecimal()

                return@map CsvTransactionDto(date, category, beneficiaryOrSender, description, amount)
            } catch (e: Exception) {
                throw InvalidDataException("Invalid value, row #${it.recordNumber}: ${e.message}")
            }
        }.toList()

        csvTransactions = resolveCsvTransactionsCategory(userId, csvTransactions)

        val importFile = createLocalImportFile(importDataPair.first.extension, importDataPair.second)
        val newTransactions = importRepository.importTransactions(userId, accountId, csvTransactions, importFile)

        return newTransactions.map { TransactionDto(it) }
    }

    private fun resolveCsvTransactionsCategory(userId: Long, csvTransactions: List<CsvTransactionDto>): List<CsvTransactionDto> {
        val rules = importRuleRepository.findAll(userId)
        val categoryNames = categoryRepository.findAll().map { it.name }

        return csvTransactions.map { csvTransaction ->
            if (csvTransaction.category.isBlank() || !categoryNames.contains(csvTransaction.category)) {
                val ruleResolvingText = csvTransaction.beneficiaryOrSender + csvTransaction.description

                val rule = rules.map { rule ->
                    val trie = Trie.builder().ignoreCase().addKeywords(rule.keywords).build()
                    val emits = trie.parseText(ruleResolvingText)
                    Pair(rule, emits.size)
                }.filter { it.second > 0 }.maxByOrNull { it.second }?.first

                rule?.apply {
                    csvTransaction.category = this.recordCategory.name
                }
            }

            return@map csvTransaction
        }
    }
}