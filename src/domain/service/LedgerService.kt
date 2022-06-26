package io.ducket.api.domain.service

import io.ducket.api.app.LedgerRecordType
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.ledger.LedgerRecordCreateDto
import io.ducket.api.domain.controller.ledger.LedgerRecordDto
import io.ducket.api.domain.model.ledger.LedgerRecord
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.LedgerRepository
import io.ducket.api.domain.repository.OperationRepository
import io.ducket.api.domain.repository.OperationAttachmentRepository
import io.ducket.api.utils.sumByDecimal
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoDataFoundException
import io.ducket.api.utils.toLocalDate
import io.ktor.http.content.*
import java.io.File
import java.math.BigDecimal


class LedgerService(
    private val groupService: GroupService,
    private val localFileService: LocalFileService,
    private val currencyService: CurrencyService,
    private val operationRepository: OperationRepository,
    private val operationAttachmentRepository: OperationAttachmentRepository,
    private val ledgerRepository: LedgerRepository,
    private val accountRepository: AccountRepository,
): Transactional {

    suspend fun getLedgerRecord(userId: Long, ledgerRecordId: Long): LedgerRecordDto {
        return ledgerRepository.findOne(userId, ledgerRecordId)?.let { LedgerRecordDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun getLedgerRecords(userId: Long): List<LedgerRecordDto> {
        return ledgerRepository.findAll(userId).map { LedgerRecordDto(it) }
    }

    suspend fun createLedgerTransfer(userId: Long, reqObj: LedgerRecordCreateDto): LedgerRecordDto {
        if (reqObj.transferAccountId == reqObj.accountId) throw InvalidDataException("Accounts must differ")

        val fromAccount = accountRepository.findOne(userId, reqObj.accountId) ?: throw NoDataFoundException("Origin account was not found")
        val toAccount = accountRepository.findOne(userId, reqObj.transferAccountId!!) ?: throw NoDataFoundException("Target account was not found")

        val rate = reqObj.rate.let { customRate ->
            if (fromAccount.currency != toAccount.currency) {
                if (customRate == null) {
                    val transferExchangeRate = currencyService.getCurrencyRate(
                        baseCurrency = fromAccount.currency.isoCode,
                        quoteCurrency = toAccount.currency.isoCode,
                        date = reqObj.operation.date.toLocalDate()
                    )
                    return@let transferExchangeRate.rate
                } else {
                    return@let customRate
                }
            } else {
                if (customRate != BigDecimal.ONE) {
                    throw InvalidDataException("Invalid exchange rate, must be 1.0")
                } else {
                    return@let BigDecimal.ONE
                }
            }
        }

        return blockingTransaction {
            operationRepository.createOne(userId, reqObj.operation).let { operation ->
                // outgoing transfer
                ledgerRepository.createOne(
                    operationId = operation.id,
                    accountId = reqObj.accountId,
                    transferAccountId = reqObj.transferAccountId,
                    type = LedgerRecordType.EXPENSE,
                    postedFunds = reqObj.amount,
                    clearedFunds = reqObj.amount
                ).also {
                    // incoming transfer
                    ledgerRepository.createOne(
                        operationId = operation.id,
                        accountId = reqObj.transferAccountId,
                        transferAccountId = reqObj.accountId,
                        type = LedgerRecordType.INCOME,
                        postedFunds = reqObj.amount,
                        clearedFunds = reqObj.amount.multiply(rate)
                    )
                }
            }
        }.let {
            LedgerRecordDto(it)
        }
    }

    suspend fun createLedgerRecord(userId: Long, reqObj: LedgerRecordCreateDto): LedgerRecordDto {
        if (reqObj.transferAccountId != null) {
            return createLedgerTransfer(userId, reqObj)
        }

        return blockingTransaction {
            operationRepository.createOne(userId, reqObj.operation).let { operation ->
                ledgerRepository.createOne(
                    operationId = operation.id,
                    accountId = reqObj.accountId,
                    type = reqObj.type,
                    postedFunds = reqObj.amount,
                    clearedFunds = reqObj.amount,
                )
            }
        }.let {
            LedgerRecordDto(it)
        }
    }

    suspend fun deleteLedgerRecord(userId: Long, ledgerRecordId: Long) {
        val ledgerRecord = ledgerRepository.findOne(userId, ledgerRecordId) ?: throw NoDataFoundException()
        ledgerRepository.delete(userId, ledgerRecordId, ledgerRecord.operation.id)
    }

//    fun deleteLedgerRecords(userId: Long, payload: BulkDeleteDto) {
//        transactionRepository.delete(userId, *reqObj.ids.toLongArray())
//    }

    suspend fun uploadLedgerRecordAttachments(userId: Long, ledgerRecordId: Long, operationId: Long, multipartData: List<PartData>) {
        ledgerRepository.findOneByOperation(userId, ledgerRecordId, operationId) ?: throw NoDataFoundException()

        val actualAttachmentsAmount = operationAttachmentRepository.getAttachmentsAmount(operationId)
        val contentPairList = localFileService.extractMultipartImageData(multipartData)

        if (contentPairList.size + actualAttachmentsAmount > 3) {
            throw InvalidDataException("Attachments limit exceeded, max 3")
        }

        contentPairList.forEach { pair ->
            val newFile = localFileService.createLocalImageFile(pair.first.extension, pair.second)
            operationAttachmentRepository.createAttachment(userId, operationId, newFile)
        }
    }

    suspend fun downloadLedgerRecordAttachment(userId: Long, ledgerRecordId: Long, operationId: Long, attachmentId: Long): File {
        ledgerRepository.findOneByOperation(userId, ledgerRecordId, operationId) ?: throw NoDataFoundException()

        return operationAttachmentRepository.findAttachment(operationId, attachmentId)?.let {
            localFileService.getLocalFile(it.filePath) ?: throw NoDataFoundException("No such file was found")
        } ?: throw NoDataFoundException("No such attachment was found")
    }

    suspend fun deleteLedgerRecordAttachment(userId: Long, ledgerRecordId: Long, operationId: Long, attachmentId: Long) {
        ledgerRepository.findOneByOperation(userId, ledgerRecordId, operationId) ?: throw NoDataFoundException()

        operationAttachmentRepository.findAttachment(ledgerRecordId, attachmentId)?.also {
            operationAttachmentRepository.deleteAttachments(ledgerRecordId, attachmentId)
        } ?: throw NoDataFoundException("No such attachment was found")
    }

    private suspend fun resolveLedgerRecordBalance(record: LedgerRecord): BigDecimal {
        val ledgerRecords = ledgerRepository.findAllByAccount(record.operation.user.id, record.account.id).sortedBy { it.operation.date }
        val recordIndex = ledgerRecords.indexOf(record)

        return ledgerRecords.subList(0, recordIndex + 1).sumByDecimal {
            if (it.type == LedgerRecordType.EXPENSE) it.clearedFunds.negate() else it.clearedFunds
        }
    }
}