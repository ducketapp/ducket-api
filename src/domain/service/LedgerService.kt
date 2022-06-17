package io.ducket.api.domain.service

import clients.rates.ReferenceRatesClient
import io.ducket.api.app.LedgerRecordType
import io.ducket.api.domain.controller.ledger.LedgerRecordCreateDto
import io.ducket.api.domain.controller.ledger.LedgerRecordDto
import io.ducket.api.domain.model.ledger.LedgerRecord
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.LedgerRepository
import io.ducket.api.domain.repository.OperationRepository
import io.ducket.api.domain.repository.OperationAttachmentRepository
import io.ducket.api.utils.sumByDecimal
import io.ducket.api.plugins.BusinessLogicException
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import io.ducket.api.utils.toLocalDate
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.math.BigDecimal

class LedgerService(
    private val groupService: GroupService,
    private val localFileService: LocalFileService,
    private val operationRepository: OperationRepository,
    private val operationAttachmentRepository: OperationAttachmentRepository,
    private val ledgerRepository: LedgerRepository,
    private val accountRepository: AccountRepository,
) {
    private val ratesClient: ReferenceRatesClient by inject(ReferenceRatesClient::class.java)

    fun getLedgerRecord(userId: Long, ledgerRecordId: Long): LedgerRecordDto {
        return getLedgerRecords(userId).firstOrNull { it.id == ledgerRecordId } ?: throw NoEntityFoundException()
    }

    fun getLedgerRecords(userId: Long): List<LedgerRecordDto> {
        val ledgerRecords = ledgerRepository.findAll(userId)

        return ledgerRecords.groupBy { it.account.id }.flatMap { accountToRecords ->
            var currentRecordBalance = BigDecimal.ZERO
            val calculatedAccountRecords = accountToRecords.value
                .sortedBy { it.operation.date }
                .mapIndexed { index, ledgerRecord ->
                    currentRecordBalance = if (index == 0) ledgerRecord.amountPosted else currentRecordBalance.plus(ledgerRecord.amountPosted)
                    return@mapIndexed LedgerRecordDto(ledgerRecord, currentRecordBalance)
                }
            return@flatMap calculatedAccountRecords
        }.sortedWith(compareByDescending<LedgerRecordDto> { it.operation.date }.thenByDescending { it.id })
    }

    fun createLedgerTransfer(userId: Long, payload: LedgerRecordCreateDto): LedgerRecordDto {
        if (payload.transferAccountId == payload.accountId) throw BusinessLogicException("Accounts must differ")

        val fromAccount = accountRepository.findOne(userId, payload.accountId)
            ?: throw NoEntityFoundException("Sender's account was not found")

        val toAccount = accountRepository.findOne(userId, payload.transferAccountId!!)
            ?: throw NoEntityFoundException("Recipient's account was not found")

        val rate = payload.rate.let { userSpecifiedRate ->
            if (userSpecifiedRate == null) {
                if (fromAccount.currency.id != toAccount.currency.id) {
                    val rateRecord = runBlocking {
                        ratesClient.getFirstRateStartingFromDate(
                            currency = fromAccount.currency.isoCode,
                            baseCurrency = toAccount.currency.isoCode,
                            startDate = payload.operation.date.toLocalDate()
                        )
                    }
                    // return@let rateRecord.value
                    return@let BigDecimal.ONE
                }
            } else {
                if (userSpecifiedRate != BigDecimal.ONE && fromAccount.currency.id == toAccount.currency.id) {
                    throw InvalidDataException("Invalid exchange rate, must be 1")
                } else {
                    return@let userSpecifiedRate
                }
            }
            return@let BigDecimal.ONE
        }

        return transaction {
            val operationId = operationRepository.create(userId, payload.operation).id.value

            ledgerRepository.createTransfer(
                operationId = operationId,
                transferAccountId = payload.transferAccountId,
                accountId = payload.accountId,
                amount = payload.amount,
                rate = rate,
            )
        }.let {
            LedgerRecordDto(it, resolveLedgerRecordBalance(it))
        }
    }

    fun createLedgerRecord(userId: Long, payload: LedgerRecordCreateDto): LedgerRecordDto {
        if (payload.transferAccountId != null) {
            return createLedgerTransfer(userId, payload)
        }

        return transaction {
            val operationId = operationRepository.create(userId, payload.operation).id.value

            ledgerRepository.create(
                operationId = operationId,
                accountId = payload.accountId,
                amount = payload.amount,
                type = payload.type,
            )
        }.let {
            LedgerRecordDto(it, resolveLedgerRecordBalance(it))
        }
    }

    fun deleteLedgerRecord(userId: Long, ledgerRecordId: Long) {
        val ledgerRecord = ledgerRepository.findOne(userId, ledgerRecordId) ?: throw NoEntityFoundException()
        ledgerRepository.delete(userId, ledgerRecordId, ledgerRecord.operation.id)
    }

//    fun deleteLedgerRecords(userId: Long, payload: BulkDeleteDto) {
//        transactionRepository.delete(userId, *reqObj.ids.toLongArray())
//    }

    fun uploadLedgerRecordAttachments(userId: Long, ledgerRecordId: Long, operationId: Long, multipartData: List<PartData>) {
        ledgerRepository.findOneByOperation(userId, ledgerRecordId, operationId) ?: throw NoEntityFoundException()

        val actualAttachmentsAmount = operationAttachmentRepository.getAttachmentsAmount(operationId)
        val contentPairList = localFileService.extractImagesData(multipartData)

        if (contentPairList.size + actualAttachmentsAmount > 3) {
            throw InvalidDataException("Attachments limit exceeded, max 3")
        }

        contentPairList.forEach { pair ->
            val newFile = localFileService.createLocalImageFile(pair.first.extension, pair.second)
            operationAttachmentRepository.createAttachment(userId, operationId, newFile)
        }
    }

    fun downloadLedgerRecordAttachment(userId: Long, ledgerRecordId: Long, operationId: Long, attachmentId: Long): File {
        ledgerRepository.findOneByOperation(userId, ledgerRecordId, operationId) ?: throw NoEntityFoundException()

        return operationAttachmentRepository.findAttachment(operationId, attachmentId)?.let {
            localFileService.getLocalFile(it.filePath) ?: throw NoEntityFoundException("No such file was found")
        } ?: throw NoEntityFoundException("No such attachment was found")
    }

    fun deleteLedgerRecordAttachment(userId: Long, ledgerRecordId: Long, operationId: Long, attachmentId: Long) {
        ledgerRepository.findOneByOperation(userId, ledgerRecordId, operationId) ?: throw NoEntityFoundException()

        operationAttachmentRepository.findAttachment(ledgerRecordId, attachmentId)?.also {
            operationAttachmentRepository.deleteAttachments(ledgerRecordId, attachmentId)
        } ?: throw NoEntityFoundException("No such attachment was found")
    }

    private fun resolveLedgerRecordBalance(ledgerRecord: LedgerRecord): BigDecimal {
        val ledgerRecords = ledgerRepository.findAllByAccount(ledgerRecord.operation.user.id, ledgerRecord.account.id).sortedBy { it.operation.date }
        val recordIndex = ledgerRecords.indexOf(ledgerRecord)

        return ledgerRecords.subList(0, recordIndex + 1).sumByDecimal {
            if (it.type == LedgerRecordType.EXPENSE) it.amountPosted.negate() else it.amountPosted
        }
    }
}