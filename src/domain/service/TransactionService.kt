package io.ducket.api.domain.service

import io.ducket.api.domain.controller.transaction.TransactionCreateDto
import io.ducket.api.domain.controller.transaction.TransactionDeleteDto
import io.ducket.api.domain.controller.transaction.TransactionDto
import io.ducket.api.domain.repository.TransactionRepository
import io.ducket.api.plugins.InvalidDataError
import io.ducket.api.plugins.NoEntityFoundError
import io.ktor.http.content.*
import java.io.File

class TransactionService(private val transactionRepository: TransactionRepository): FileService() {

    fun getTransaction(userId: String, transactionId: String): TransactionDto {
        return transactionRepository.findOne(userId, transactionId)?.let { TransactionDto(it) }
            ?: throw NoEntityFoundError("No such transaction was found")
    }

    fun getTransactions(userId: String): List<TransactionDto> {
        return transactionRepository.findAll(userId).map { TransactionDto(it) }
    }

    fun addTransaction(userId: String, reqObj: TransactionCreateDto): TransactionDto {
        val newTransaction = transactionRepository.create(userId, reqObj)
        return TransactionDto(newTransaction)
    }

    fun deleteTransaction(userId: String, transactionId: String) {
        transactionRepository.delete(userId, transactionId)
    }

    fun deleteTransactions(userId: String, reqObj: TransactionDeleteDto) {
        transactionRepository.delete(userId, *reqObj.transactionIds.toTypedArray())
    }

    fun downloadTransactionAttachment(userId: String, transactionId: String, attachmentId: String): File {
        transactionRepository.findOne(userId, transactionId)
            ?: throw NoEntityFoundError("No such transaction was found")

        val attachment = transactionRepository.findAttachment(userId, transactionId, attachmentId)
            ?: throw NoEntityFoundError("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoEntityFoundError("No such file was found")
    }

    fun uploadTransactionAttachments(userId: String, transactionId: String, multipartData: List<PartData>) {
        transactionRepository.findOne(userId, transactionId) ?: throw NoEntityFoundError("No such transaction was found")

        val actualAttachmentsAmount = transactionRepository.getAttachmentsAmount(transactionId)
        val contentPairList = pullAttachments(multipartData)

        if (contentPairList.size + actualAttachmentsAmount > 3) throw InvalidDataError("Attachments limit exceeded, max 3")

        contentPairList.forEach { pair ->
            val newFile = createLocalAttachmentFile(pair.first.extension, pair.second)
            transactionRepository.createAttachment(userId, transactionId, newFile)
        }
    }

    fun deleteTransactionAttachment(userId: String, transactionId: String, attachmentId: String): Boolean {
        return transactionRepository.findAttachment(userId, transactionId, attachmentId)?.let { attachment ->
            transactionRepository.deleteAttachment(userId, transactionId, attachmentId).takeIf {
                deleteLocalFile(attachment.filePath)
            }
        } ?: throw NoEntityFoundError("No such attachment was found")
    }
}