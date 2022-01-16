package io.ducket.api.domain.service

import io.ducket.api.domain.controller.transaction.TransactionCreateDto
import io.ducket.api.domain.controller.transaction.TransactionDeleteDto
import io.ducket.api.domain.controller.transaction.TransactionDto
import io.ducket.api.domain.repository.TransactionRepository
import io.ducket.api.plugins.InvalidDataError
import io.ducket.api.plugins.NoEntityFoundError
import io.ktor.http.content.*
import java.io.File

class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountService: AccountService,
): FileService() {

    /**
     * Find the user's transaction among all the transactions, including observed ones
     */
    fun getTransactionDetailsAccessibleToUser(userId: Long, transactionId: Long): TransactionDto {
        return getTransactionsAccessibleToUser(userId).firstOrNull { it.id == transactionId }
            ?: throw NoEntityFoundError("No such transaction was found")
    }

    /**
     * Find all the user's transactions, including observed ones
     */
    fun getTransactionsAccessibleToUser(userId: Long): List<TransactionDto> {
        return transactionRepository.findAllIncludingObserved(userId)
            .map { TransactionDto(it) }
            .onEach {
                it.account.balance = accountService.calculateBalance(it.account.owner.id, it.account.id, it.date)
            }
    }

    /**
     * Add new transaction
     */
    fun addTransaction(userId: Long, reqObj: TransactionCreateDto): TransactionDto {
        return TransactionDto(transactionRepository.create(userId, reqObj))
    }

    /**
     * Delete one transaction
     */
    fun deleteTransaction(userId: Long, transactionId: Long) {
        transactionRepository.delete(userId, transactionId)
    }

    /**
     * Delete multiple transactions
     */
    fun deleteTransactions(userId: Long, reqObj: TransactionDeleteDto) {
        transactionRepository.delete(userId, *reqObj.transactionIds.toLongArray())
    }

    /**
     * Download transaction attachment file, including observed ones
     */
    fun downloadTransactionAttachment(userId: Long, transactionId: Long, attachmentId: Long): File {
        val transaction = getTransactionDetailsAccessibleToUser(userId, transactionId)
        val attachment = transactionRepository.findAttachment(transaction.owner.id, transactionId, attachmentId)
            ?: throw NoEntityFoundError("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoEntityFoundError("No such file was found")
    }

    /**
     * Upload transaction attachment file
     */
    fun uploadTransactionAttachments(userId: Long, transactionId: Long, multipartData: List<PartData>) {
        transactionRepository.findOne(userId, transactionId) ?: throw NoEntityFoundError("No such transaction was found")

        val actualAttachmentsAmount = transactionRepository.getAttachmentsAmount(transactionId)
        val contentPairList = extractImagesData(multipartData)

        if (contentPairList.size + actualAttachmentsAmount > 3) throw InvalidDataError("Attachments limit exceeded, max 3")

        contentPairList.forEach { pair ->
            val newFile = createLocalAttachmentFile(pair.first.extension, pair.second)
            transactionRepository.createAttachment(userId, transactionId, newFile)
        }
    }

    /**
     * Delete attachment from transaction
     */
    fun deleteTransactionAttachment(userId: Long, transactionId: Long, attachmentId: Long): Boolean {
        return transactionRepository.findAttachment(userId, transactionId, attachmentId)?.let { attachment ->
            transactionRepository.deleteAttachment(userId, transactionId, attachmentId).takeIf {
                deleteLocalFile(attachment.filePath)
            }
        } ?: throw NoEntityFoundError("No such attachment was found")
    }
}