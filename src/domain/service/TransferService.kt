package io.ducket.api.domain.service

import io.ducket.api.CurrencyRateProvider
import io.ducket.api.domain.controller.transfer.TransferCreateDto
import io.ducket.api.domain.controller.transfer.TransferDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.TransferRepository
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.plugins.NoEntityFoundException
import io.ktor.http.content.*
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.math.BigDecimal

class TransferService(
    private val transferRepository: TransferRepository,
    private val accountRepository: AccountRepository,
    private val accountService: AccountService,
    private val groupService: GroupService,
): FileService() {
    private val currencyRateProvider: CurrencyRateProvider by inject(CurrencyRateProvider::class.java)

    fun getTransferAccessibleToUser(userId: Long, transferId: Long): TransferDto {
        return getTransfersAccessibleToUser(userId).firstOrNull { it.id == transferId } ?: throw NoEntityFoundException()
    }

    fun getTransfersAccessibleToUser(userId: Long): List<TransferDto> {
        val userIds = groupService.getDistinctUsersWithMutualGroupMemberships(userId).map { it.id } + userId

        return transferRepository.findAll(*userIds.toLongArray())
            .map { TransferDto(it) }
            .onEach {
                it.account.balance = accountService.calculateBalance(it.account.owner.id, it.account.id, it.date)
                it.transferAccount.balance = accountService.calculateBalance(it.account.owner.id, it.transferAccount.id, it.date)
            }
    }

    fun createTransfer(userId: Long, payload: TransferCreateDto): List<TransferDto> {
        val fromAccount = accountRepository.findOne(userId, payload.accountId) ?: throw NoEntityFoundException("Origin account was not found")
        val toAccount = accountRepository.findOne(userId, payload.transferAccountId) ?: throw NoEntityFoundException("Target account was not found")

        var exchangeRate = BigDecimal.ONE

        if (payload.exchangeRate == null) {
            if (fromAccount.currency.id != toAccount.currency.id) {
                exchangeRate = currencyRateProvider.getCurrencyRate(fromAccount.currency.isoCode, toAccount.currency.isoCode)
            }
        } else {
            if (fromAccount.currency.id == toAccount.currency.id && exchangeRate != BigDecimal.ONE) {
                throw InvalidDataException("Invalid exchange rate, should be 1.0")
            }
        }

        return transferRepository.create(userId, payload, exchangeRate).map { TransferDto(it) }
    }

    fun deleteTransfer(userId: Long, transferId: Long) {
        val transfer = transferRepository.findOne(userId, transferId) ?: throw NoEntityFoundException()

        if (transfer.relationCode == null) transferRepository.delete(userId, transferId)
        else transferRepository.delete(userId, transfer.relationCode)
    }

    fun downloadTransferAttachment(userId: Long, entityId: Long, attachmentId: Long): File {
        val transfer = getTransferAccessibleToUser(userId, entityId)
        val attachment = transferRepository.findAttachment(transfer.owner.id, entityId, attachmentId)
            ?: throw NoEntityFoundException("No such attachment was found")

        return getLocalFile(attachment.filePath) ?: throw NoEntityFoundException("No such file was found")
    }

    fun uploadTransferAttachments(userId: Long, entityId: Long, multipartData: List<PartData>) {
        transferRepository.findOne(userId, entityId) ?: throw NoEntityFoundException("No such transfer was found")

        val actualAttachmentsAmount = transferRepository.getAttachmentsAmount(entityId)
        val files = extractImagesData(multipartData)

        if (files.size + actualAttachmentsAmount > 3) throw InvalidDataException("Attachments limit exceeded, 3 max")

        extractImagesData(multipartData).forEach { pair ->
            val newFile = createLocalAttachmentFile(pair.first.extension, pair.second)
            transferRepository.createAttachment(userId, entityId, newFile)
        }
    }

    fun deleteTransferAttachment(userId: Long, entityId: Long, attachmentId: Long): Boolean {
        return transferRepository.findAttachment(userId, entityId, attachmentId)?.let { attachment ->
            transferRepository.deleteAttachment(userId, entityId, attachmentId).takeIf {
                deleteLocalFile(attachment.filePath)
            }
        } ?: throw NoEntityFoundException("No such attachment was found")
    }
}