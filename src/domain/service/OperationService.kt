package io.ducket.api.domain.service

import io.ducket.api.domain.mapper.OperationMapper
import io.ducket.api.app.OperationType
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.operation.dto.OperationCreateUpdateDto
import io.ducket.api.domain.controller.operation.dto.OperationDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.CategoryRepository
import io.ducket.api.domain.repository.OperationRepository
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoDataFoundException

class OperationService(
    private val operationRepository: OperationRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
): Transactional {

    suspend fun createOperation(userId: Long, dto: OperationCreateUpdateDto): OperationDto {
        validateOperation(userId, dto)

        return operationRepository.createOne(OperationMapper.mapDtoToModel(dto, userId, null)).let {
            OperationMapper.mapModelToDto(it)
        }
    }

    suspend fun updateOperation(userId: Long, operationId: Long, dto: OperationCreateUpdateDto): OperationDto {
        validateOperation(userId, dto)

        return operationRepository.updateOne(userId, operationId, OperationMapper.mapDtoToModel(dto))?.let {
            OperationMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getOperation(userId: Long, operationId: Long): OperationDto {
        return operationRepository.findOne(userId, operationId)?.let { OperationMapper.mapModelToDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun getOperations(userId: Long): List<OperationDto> {
        return operationRepository.findAll(userId).map { OperationMapper.mapModelToDto(it) }
    }

    suspend fun deleteOperation(userId: Long, operationId: Long) {
        operationRepository.delete(userId, operationId)
    }

    private suspend fun validateOperation(userId: Long, reqObj: OperationCreateUpdateDto) {
        categoryRepository.findOne(reqObj.categoryId) ?: throw NoDataFoundException("Category was not found")
        val account = accountRepository.findOne(userId, reqObj.accountId) ?: throw NoDataFoundException("Account was not found")

        if (reqObj.type == OperationType.TRANSFER) {
            if (reqObj.transferAccountId == null) {
                throw InvalidDataException("Transfer account is required")
            }

            val transferAccount = accountRepository.findOne(userId, reqObj.transferAccountId)
                ?: throw NoDataFoundException("Transfer account was not found")

            if (transferAccount.id == account.id) {
                throw InvalidDataException("Cannot make a transfer to the same account")
            }

            if (account.currency == transferAccount.currency && reqObj.amountData.cleared != reqObj.amountData.posted) {
                throw InvalidDataException("Transfer rate should equal to 1 for specified accounts")
            }
        } else {
            if (reqObj.transferAccountId != null) {
                throw InvalidDataException("Setting a transfer account is not acceptable for this operation")
            }

            if (reqObj.amountData.posted != reqObj.amountData.cleared) {
                throw InvalidDataException("Setting the rate is not acceptable for this operation")
            }
        }
    }

//    suspend fun uploadOperationAttachments(userId: Long, operationId: Long, multipartData: List<PartData>) {
//        operationRepository.findOne(userId, operationId) ?: throw NoDataFoundException()
//
//        val actualAttachmentsCount = operationAttachmentRepository.getCount(operationId)
//        val contentPairList = localFileService.extractMultipartImageData(multipartData)
//
//        if (contentPairList.size + actualAttachmentsCount > 3) throw InvalidDataException("Attachments limit exceeded, max 3")
//
//        contentPairList.forEach { pair ->
//            localFileService.createLocalImageFile(pair.first, pair.second).also { newFile ->
//                operationAttachmentRepository.createOne(userId, operationId, newFile)
//            }
//        }
//    }
//
//    suspend fun downloadOperationAttachment(userId: Long, operationId: Long, attachmentId: Long): File {
//        operationRepository.findOne(userId, operationId) ?: throw NoDataFoundException()
//
//        return operationAttachmentRepository.findOne(operationId, attachmentId)?.let {
//            localFileService.getLocalFile(it.filePath) ?: throw NoDataFoundException("File not found")
//        } ?: throw NoDataFoundException("No such attachment was found")
//    }
//
//    suspend fun deleteOperationAttachment(userId: Long, operationId: Long, attachmentId: Long) {
//        operationRepository.findOne(userId, operationId) ?: throw NoDataFoundException()
//
//        operationAttachmentRepository.findOne(operationId, attachmentId)?.also {
//            operationAttachmentRepository.delete(operationId, attachmentId)
//        } ?: throw NoDataFoundException("No such attachment was found")
//    }
}