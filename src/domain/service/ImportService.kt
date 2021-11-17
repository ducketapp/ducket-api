package io.ducket.api.domain.service

import io.ducket.api.InvalidDataError
import io.ducket.api.NoEntityFoundError
import io.ducket.api.domain.controller.imports.CsvTransaction
import io.ducket.api.domain.controller.imports.ImportDto
import io.ducket.api.domain.controller.transaction.TransactionDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.CategoryRepository
import io.ducket.api.domain.repository.ImportRepository
import io.ducket.api.domain.repository.RuleRepository
import io.ducket.api.extension.trimWhitespaces
import io.ducket.api.getLogger
import io.ktor.http.content.*
import org.ahocorasick.trie.Trie
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.Instant
import kotlin.streams.toList

class ImportService(
    private val importRepository: ImportRepository,
    private val accountRepository: AccountRepository,
    private val ruleRepository: RuleRepository,
    private val categoryRepository: CategoryRepository,
): FileService() {
    private val logger = getLogger()

    fun getImports(userId: String): List<ImportDto> {
        return importRepository.getAllByUserId(userId).map { ImportDto(it) }
    }

    fun importTransactions(userId: String, accountId: String, multipartData: List<PartData>): List<TransactionDto> {
        accountRepository.findOne(userId, accountId) ?: throw NoEntityFoundError("No such account was found")

        val importDataPair = extractImportData(multipartData)
        val reader = BufferedReader(InputStreamReader(importDataPair.second.inputStream()))

        val csvParser = CSVParser(reader, CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(',')
            .withIgnoreHeaderCase().withTrim())

        val headers = listOf("date", "category", "beneficiary/sender", "notes", "amount")
        csvParser.headerMap.takeIf { it.keys.containsAll(headers) && it.size == headers.size }
            ?: throw InvalidDataError("Invalid header set")

        val csvRecords = csvParser.records.takeIf { it.size > 0 }
            ?: throw InvalidDataError("Invalid records amount: 0")

        var csvTransactions = csvRecords.stream().map {
            try {
                val date = Instant.parse(it.get(headers[0]))
                val category = it.get(headers[1]).trimWhitespaces()
                val beneficiaryOrSender = it.get(headers[2]).trimWhitespaces()
                val notes = it.get(headers[3]).trimWhitespaces()
                val amount = it.get(headers[4]).toBigDecimal()

                return@map CsvTransaction(date, category, beneficiaryOrSender, notes, amount)
            } catch (e: Exception) {
                throw InvalidDataError("Invalid value, row #${it.recordNumber}: ${e.message}")
            }
        }.toList()

        csvTransactions = resolveCsvTransactionsCategory(userId, csvTransactions)

        val importFile = createLocalImportFile(importDataPair.first.extension, importDataPair.second)
        val newTransactions = importRepository.importTransactions(userId, accountId, csvTransactions, importFile)

        return newTransactions.map { TransactionDto(it) }
    }

    private fun resolveCsvTransactionsCategory(userId: String, csvTransactions: List<CsvTransaction>): List<CsvTransaction> {
        val rules = ruleRepository.findAll(userId)
        val categoryNames = categoryRepository.findAll().map { it.name }

        return csvTransactions.map { csvTransaction ->
            if (csvTransaction.category.isBlank() || !categoryNames.contains(csvTransaction.category)) {
                val ruleResolvingText = csvTransaction.beneficiaryOrSender + csvTransaction.notes

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