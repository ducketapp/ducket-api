package io.budgery.api.domain.service

import io.budgery.api.domain.controller.record.AttachmentDto
import io.budgery.api.domain.controller.transaction.TransactionCreateDto
import io.budgery.api.domain.controller.transaction.TransactionDto
import io.budgery.api.domain.repository.TransactionRepository
import io.ktor.http.content.*
import java.io.File

class TransactionService(
    private val transactionRepository: TransactionRepository,
): AttachmentService(3) {

    fun getTransaction(userId: Int, transactionId: Int): TransactionDto {
        return transactionRepository.findOne(userId, transactionId)?.let { TransactionDto(it) }
            ?: throw NoSuchElementException("No such transaction was found")
    }

    fun getTransactions(userId: Int): List<TransactionDto> {
        return transactionRepository.findAll(userId).map { TransactionDto(it) }
    }

    fun addTransaction(userId: Int, reqObj: TransactionCreateDto): TransactionDto {
        val newTransaction = transactionRepository.create(userId, reqObj)
        return TransactionDto(newTransaction)
    }

    fun deleteTransaction(userId: Int, transactionId: Int): Boolean {
        return transactionRepository.deleteOne(userId, transactionId)
    }

    override fun getAttachmentFile(userId: Int, entityId: Int, attachmentId: Int): File {
        transactionRepository.findOne(userId, entityId) ?: throw NoSuchElementException("No such transaction was found")
        val attachment = transactionRepository.findAttachment(entityId, attachmentId) ?: throw NoSuchElementException("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoSuchElementException("No such file was found")
    }

    override fun addAttachments(userId: Int, entityId: Int, multipartData: List<PartData>) {
        transactionRepository.findOne(userId, entityId) ?: throw NoSuchElementException("No such transaction was found")
        val actualAttachmentsAmount = transactionRepository.getAttachmentsAmount(entityId)

        retrieveOriginalFiles(multipartData, actualAttachmentsAmount).forEachIndexed { idx, pair ->
            val newFile = createLocalFile(pair.first, pair.second, idx.toString())
            transactionRepository.createAttachment(entityId, newFile, pair.first.name)
        }
    }
}