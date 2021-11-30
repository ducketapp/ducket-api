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
): FileService() {

    fun getTransfers(userId: String): List<TransferDto> {
        return transferRepository.findAllByUserId(userId).map { TransferDto(it) }
    }

    fun getTransfer(userId: String, transferId: String): TransferDto {
        return transferRepository.findOne(userId, transferId)?.let { TransferDto(it) }
            ?: throw NoEntityFoundError("No such transfer was found")
    }

    fun addTransfer(userId: String, reqObj: TransferCreateDto): List<TransferDto> {
        val fromAccount = accountRepository.findOne(userId, reqObj.accountId) ?: throw NoEntityFoundError("Origin account was not found")
        val toAccount = accountRepository.findOne(userId, reqObj.transferAccountId) ?: throw NoEntityFoundError("Target account was not found")

        var exchangeRate = BigDecimal.ONE

        if (reqObj.exchangeRate == null) {
            if (fromAccount.currency.id != toAccount.currency.id) {
                exchangeRate = ExchangeRateClient.getRate(fromAccount.currency.isoCode, toAccount.currency.isoCode)
            }
        } else {
            if (fromAccount.currency.id == toAccount.currency.id && exchangeRate != BigDecimal.ONE) {
                throw InvalidDataError("Invalid exchange rate, should be 1.0")
            }
        }

        return transferRepository.create(userId, reqObj, exchangeRate).map { TransferDto(it) }
    }

    fun deleteTransfer(userId: String, transferId: String) {
        val transfer = transferRepository.findOne(userId, transferId) ?: throw NoEntityFoundError("No such transfer was found")
        transferRepository.delete(userId, transfer.relationId)
    }

    fun downloadTransferAttachment(userId: String, entityId: String, attachmentId: String): File {
        transferRepository.findOne(userId, entityId) ?: throw NoEntityFoundError("No such transfer was found")
        val attachment = transferRepository.findAttachment(userId, entityId, attachmentId) ?: throw NoEntityFoundError("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoEntityFoundError("No such file was found")
    }

    fun uploadTransferAttachments(userId: String, entityId: String, multipartData: List<PartData>) {
        transferRepository.findOne(userId, entityId) ?: throw NoEntityFoundError("No such transfer was found")

        val actualAttachmentsAmount = transferRepository.getAttachmentsAmount(entityId)
        val files = pullAttachments(multipartData)

        if (files.size + actualAttachmentsAmount > 3) throw InvalidDataError("Attachments limit exceeded, 3 max")

        pullAttachments(multipartData).forEach { pair ->
            val newFile = createLocalAttachmentFile(pair.first.extension, pair.second)
            transferRepository.createAttachment(userId, entityId, newFile)
        }
    }
}