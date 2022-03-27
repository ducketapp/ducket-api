package io.ducket.api.domain.service

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.transaction.TransactionCreateDto
import io.ducket.api.domain.controller.transaction.TransactionDto
import io.ducket.api.domain.repository.TransactionRepository
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import io.ktor.http.content.*
import java.io.File

class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountService: AccountService,
    private val groupService: GroupService,
): FileService() {

    /**
     * Find the user's transaction among all the transactions, including shared ones
     */
    fun getTransactionAccessibleToUser(userId: Long, transactionId: Long): TransactionDto {
        return getTransactionsAccessibleToUser(userId).firstOrNull { it.id == transactionId } ?: throw NoEntityFoundException()
    }

    /**
     * Find all the user's transactions, including shared ones
     */
    fun getTransactionsAccessibleToUser(userId: Long): List<TransactionDto> {
        val userIds = groupService.getDistinctUsersWithMutualGroupMemberships(userId).map { it.id } + userId

        return transactionRepository.findAll(*userIds.toLongArray())
            .map { TransactionDto(it) }
            .onEach {
                it.account.balance = accountService.calculateBalance(it.account.owner.id, it.account.id, it.date)
            }
    }

    /**
     * Create new transaction
     */
    fun createTransaction(userId: Long, reqObj: TransactionCreateDto): TransactionDto {
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
    fun deleteTransactions(userId: Long, reqObj: BulkDeleteDto) {
        transactionRepository.delete(userId, *reqObj.ids.toLongArray())
    }

    /**
     * Download transaction attachment file, including observed ones
     */
    fun downloadTransactionAttachment(userId: Long, transactionId: Long, attachmentId: Long): File {
        val transaction = getTransactionAccessibleToUser(userId, transactionId)
        val attachment = transactionRepository.findAttachment(transaction.owner.id, transactionId, attachmentId)
            ?: throw NoEntityFoundException("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoEntityFoundException("No such file was found")
    }

    /**
     * Upload transaction attachment file
     */
    fun uploadTransactionAttachments(userId: Long, transactionId: Long, multipartData: List<PartData>) {
        transactionRepository.findOne(userId, transactionId) ?: throw NoEntityFoundException()

        val actualAttachmentsAmount = transactionRepository.getAttachmentsAmount(transactionId)
        val contentPairList = extractImagesData(multipartData)

        if (contentPairList.size + actualAttachmentsAmount > 3) {
            throw InvalidDataException("Attachments limit exceeded, max 3")
        }

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
        } ?: throw NoEntityFoundException("No such attachment was found")
    }
}