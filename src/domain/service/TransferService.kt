package io.budgery.api.domain.service

import io.budgery.api.ExchangeRateClient
import io.budgery.api.domain.controller.transfer.TransferCreateDto
import io.budgery.api.domain.controller.transfer.TransferDto
import io.budgery.api.domain.repository.AccountRepository
import io.budgery.api.domain.repository.TransferRepository
import io.ktor.http.content.*
import java.io.File
import java.math.BigDecimal

class TransferService(
    private val transferRepository: TransferRepository,
    private val accountRepository: AccountRepository,
): AttachmentService(3) {

    fun getTransfers(userId: Int): List<TransferDto> {
        return transferRepository.findAllByUserId(userId).map { TransferDto(it) }
    }

    fun getTransfer(userId: Int, transferId: Int): TransferDto {
        return transferRepository.findOne(userId, transferId)?.let { TransferDto(it) }
            ?: throw NoSuchElementException("No such transfer was found")
    }

    fun addTransfer(userId: Int, reqObj: TransferCreateDto): List<TransferDto> {
        val fromAccount = accountRepository.findOne(userId, reqObj.accountId) ?: throw NoSuchElementException("Origin account was not found")
        val toAccount = accountRepository.findOne(userId, reqObj.transferAccountId) ?: throw NoSuchElementException("Target account was not found")

        var exchangeRate = BigDecimal.ONE

        if (reqObj.exchangeRate == null) {
            if (fromAccount.currency.id != toAccount.currency.id) {
                exchangeRate = ExchangeRateClient().getRate(fromAccount.currency.isoCode, toAccount.currency.isoCode)
            }
        } else {
            if (fromAccount.currency.id == toAccount.currency.id && exchangeRate != BigDecimal.ONE) {
                throw IllegalArgumentException("Invalid exchange rate, should be 1.0")
            }
        }

        return transferRepository.create(userId, reqObj, exchangeRate).map { TransferDto(it) }
    }

    fun deleteTransfer(userId: Int, transferId: Int) : Boolean {
        val foundTransfer = transferRepository.findOne(userId, transferId) ?: throw NoSuchElementException("No such transfer was found")
        return transferRepository.delete(userId, foundTransfer.relationUuid)
    }

    override fun getAttachmentFile(userId: Int, entityId: Int, attachmentId: Int): File {
        transferRepository.findOne(userId, entityId) ?: throw NoSuchElementException("No such transfer was found")
        val attachment = transferRepository.findAttachment(entityId, attachmentId) ?: throw NoSuchElementException("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoSuchElementException("No such file was found")
    }

    override fun addAttachments(userId: Int, entityId: Int, multipartData: List<PartData>) {
        transferRepository.findOne(userId, entityId) ?: throw NoSuchElementException("No such transfer was found")
        val actualAttachmentsAmount = transferRepository.getAttachmentsAmount(entityId)

        retrieveOriginalFiles(multipartData, actualAttachmentsAmount).forEachIndexed { idx, pair ->
            val newFile = createLocalFile(pair.first, pair.second, idx.toString())
            transferRepository.createAttachment(entityId, newFile, pair.first.name)
        }
    }
}