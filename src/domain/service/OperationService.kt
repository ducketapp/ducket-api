package io.ducket.api.domain.service

import io.ducket.api.app.OperationType
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.operation.OperationDto
import io.ducket.api.domain.controller.operation.OperationCreateDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.CategoryRepository
import io.ducket.api.domain.repository.OperationAttachmentRepository
import io.ducket.api.domain.repository.OperationRepository
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoDataFoundException
import io.ducket.api.utils.toLocalDate
import io.ktor.http.content.*
import java.io.File
import java.math.BigDecimal

class OperationService(
    private val operationRepository: OperationRepository,
    private val operationAttachmentRepository: OperationAttachmentRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val currencyService: CurrencyService,
    private val localFileService: LocalFileService,
): Transactional {

    suspend fun createOperation(userId: Long, reqObj: OperationCreateDto): OperationDto {
        categoryRepository.findOne(reqObj.categoryId) ?: throw NoDataFoundException("Category was not found")
        val account = accountRepository.findOne(userId, reqObj.accountId) ?: throw NoDataFoundException("Account was not found")

        if (reqObj.type == OperationType.TRANSFER) {
            if (reqObj.transferAccountId == null) throw InvalidDataException("Transfer account is required")

            val transferAccount = accountRepository.findOne(userId, reqObj.transferAccountId) ?: throw NoDataFoundException("Transfer account was not found")
            val transferRate = reqObj.transferRate.let { customTransferRate ->
                if (account.currency != transferAccount.currency) {
                    if (customTransferRate == null) {
                        return@let currencyService.getCurrencyRate(
                            baseCurrency = account.currency.isoCode,
                            quoteCurrency = transferAccount.currency.isoCode,
                            date = reqObj.date.toLocalDate()
                        ).rate
                    } else {
                        return@let customTransferRate
                    }
                } else {
                    if (customTransferRate != BigDecimal.ONE) {
                        throw InvalidDataException("Invalid transfer rate, must be ${BigDecimal.ONE}")
                    } else {
                        return@let BigDecimal.ONE
                    }
                }
            }

            return blockingTransaction {
                operationRepository.createOne(
                    data = reqObj.toModel(
                        userId = userId,
                        importId = null,
                        clearedFunds = reqObj.funds.multiply(transferRate),
                    )
                )
            }.let { OperationDto(it) }
        } else {
            if (reqObj.transferAccountId != null) throw InvalidDataException("Transfer account is not applicable for specified type")
            if (reqObj.transferRate != null) throw InvalidDataException("Transfer rate is not applicable for specified type")

            return blockingTransaction {
                operationRepository.createOne(
                    data = reqObj.toModel(
                        userId = userId,
                        importId = null,
                        clearedFunds = reqObj.funds,
                    )
                )
            }.let { OperationDto(it) }
        }
    }

    suspend fun getOperation(userId: Long, operationId: Long): OperationDto {
        return operationRepository.findOne(userId, operationId)?.let { OperationDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun getOperations(userId: Long): List<OperationDto> {
        return operationRepository.findAll(userId).map { OperationDto(it) }
    }

    suspend fun deleteOperation(userId: Long, operationId: Long) {
        operationRepository.delete(userId, operationId)
    }

    suspend fun uploadOperationAttachments(userId: Long, operationId: Long, multipartData: List<PartData>) {
        operationRepository.findOne(userId, operationId) ?: throw NoDataFoundException()

        val actualAttachmentsCount = operationAttachmentRepository.getAttachmentsCount(operationId)
        val contentPairList = localFileService.extractMultipartImageData(multipartData)

        if (contentPairList.size + actualAttachmentsCount > 3) throw InvalidDataException("Attachments limit exceeded, max 3")

        contentPairList.forEach { pair ->
            localFileService.createLocalImageFile(pair.first.extension, pair.second).also { newFile ->
                operationAttachmentRepository.createAttachment(userId, operationId, newFile)
            }
        }
    }

    suspend fun downloadOperationAttachment(userId: Long, operationId: Long, attachmentId: Long): File {
        operationRepository.findOne(userId, operationId) ?: throw NoDataFoundException()

        return operationAttachmentRepository.findOne(operationId, attachmentId)?.let {
            localFileService.getLocalFile(it.filePath) ?: throw NoDataFoundException("File not found")
        } ?: throw NoDataFoundException("No such attachment was found")
    }

    suspend fun deleteOperationAttachment(userId: Long, operationId: Long, attachmentId: Long) {
        operationRepository.findOne(userId, operationId) ?: throw NoDataFoundException()

        operationAttachmentRepository.findOne(operationId, attachmentId)?.also {
            operationAttachmentRepository.deleteAttachments(operationId, attachmentId)
        } ?: throw NoDataFoundException("No such attachment was found")
    }
}