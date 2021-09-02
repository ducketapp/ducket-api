package io.budgery.api.domain.service

import io.budgery.api.domain.controller.record.TransferCreateDto
import io.budgery.api.domain.controller.record.TransferDto
import io.budgery.api.domain.repository.AccountRepository
import io.budgery.api.domain.repository.TransferRepository

class TransferService(
    private val transferRepository: TransferRepository,
    private val accountRepository: AccountRepository,
) {

    fun getTransfers(userId: Int): List<TransferDto> {
        return transferRepository.findAllByUserId(userId).map { TransferDto(it) }.sortedByDescending { it.amount }
    }

    fun addTransfer(userId: Int, reqObj: TransferCreateDto): List<TransferDto> {
        val account = accountRepository.findOne(userId, reqObj.accountId) ?: throw NoSuchElementException("Account was not found")
        val transferAccount = accountRepository.findOne(userId, reqObj.transferAccountId) ?: throw NoSuchElementException("Transfer account was not found")

        if (account.currency.id == transferAccount.currency.id && reqObj.exchangeRate > 1.0) {
            throw IllegalArgumentException("Exchange rate cannot be greater than 1.0 if both accounts use the same currency")
        }

        return transferRepository.create(userId, reqObj).map { TransferDto(it) }
    }

    fun deleteTransfer(userId: Int, transferId: Int) : Boolean {
        val foundTransfer = transferRepository.findOne(userId, transferId) ?: throw NoSuchElementException("No such transfer was found")
        return transferRepository.delete(userId, foundTransfer.relationUuid)
    }
}