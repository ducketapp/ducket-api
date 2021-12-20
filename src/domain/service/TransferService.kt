package io.ducket.api.domain.service

import io.ducket.api.ExchangeRateClient
import io.ducket.api.domain.controller.transfer.TransferCreateDto
import io.ducket.api.domain.controller.transfer.TransferDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.TransferRepository
import io.ducket.api.plugins.InvalidDataError
import io.ducket.api.plugins.NoEntityFoundError
import io.ktor.http.content.*
import java.io.File
import java.math.BigDecimal

class TransferService(
    private val transferRepository: TransferRepository,
    private val accountRepository: AccountRepository,
    private val accountService: AccountService,
): FileService() {

    fun getTransferDetailsAccessibleToUser(userId: Long, transferId: Long): TransferDto {
        return getTransfersAccessibleToUser(userId).firstOrNull { it.id == transferId }
            ?: throw NoEntityFoundError("No such transfer was found")
    }

    fun getTransfersAccessibleToUser(userId: Long): List<TransferDto> {
        return transferRepository.findAllIncludingObserved(userId)
            .map { TransferDto(it) }
            .onEach {
                it.account.balance = accountService.calculateBalance(it.account.owner.id, it.account.id, it.date)
                it.transferAccount.balance = accountService.calculateBalance(it.account.owner.id, it.transferAccount.id, it.date)
            }
    }

    fun addTransfer(userId: Long, reqObj: TransferCreateDto): List<TransferDto> {
        val fromAccount = accountRepository.findOne(userId, reqObj.accountId) ?: throw NoEntityFoundError("Origin account was not found")
        val toAccount = accountRepository.findOne(userId, reqObj.transferAccountId) ?: throw NoEntityFoundError("Target account was not found")

        var exchangeRate = BigDecimal.ONE

        if (reqObj.exchangeRate == null) {
            if (fromAccount.currency.id != toAccount.currency.id) {
                exchangeRate = ExchangeRateClient.getExchangeRate(fromAccount.currency.isoCode, toAccount.currency.isoCode)
            }
        } else {
            if (fromAccount.currency.id == toAccount.currency.id && exchangeRate != BigDecimal.ONE) {
                throw InvalidDataError("Invalid exchange rate, should be 1.0")
            }
        }

        return transferRepository.create(userId, reqObj, exchangeRate).map { TransferDto(it) }
    }

    fun deleteTransfer(userId: Long, transferId: Long) {
        val transfer = transferRepository.findOne(userId, transferId) ?: throw NoEntityFoundError("No such transfer was found")
        transferRepository.delete(userId, transfer.relationId)
    }

    fun downloadTransferAttachment(userId: Long, entityId: Long, attachmentId: Long): File {
        val transfer = getTransferDetailsAccessibleToUser(userId, entityId)
        val attachment = transferRepository.findAttachment(transfer.owner.id, entityId, attachmentId)
            ?: throw NoEntityFoundError("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoEntityFoundError("No such file was found")
    }

    fun uploadTransferAttachments(userId: Long, entityId: Long, multipartData: List<PartData>) {
        transferRepository.findOne(userId, entityId) ?: throw NoEntityFoundError("No such transfer was found")

        val actualAttachmentsAmount = transferRepository.getAttachmentsAmount(entityId)
        val files = pullAttachments(multipartData)

        if (files.size + actualAttachmentsAmount > 3) throw InvalidDataError("Attachments limit exceeded, 3 max")

        pullAttachments(multipartData).forEach { pair ->
            val newFile = createLocalAttachmentFile(pair.first.extension, pair.second)
            transferRepository.createAttachment(userId, entityId, newFile)
        }
    }

    fun deleteTransferAttachment(userId: Long, entityId: Long, attachmentId: Long): Boolean {
        return transferRepository.findAttachment(userId, entityId, attachmentId)?.let { attachment ->
            transferRepository.deleteAttachment(userId, entityId, attachmentId).takeIf {
                deleteLocalFile(attachment.filePath)
            }
        } ?: throw NoEntityFoundError("No such attachment was found")
    }
}