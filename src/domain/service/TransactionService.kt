package io.budgery.api.domain.service

import io.budgery.api.domain.controller.record.TransactionCreateDto
import io.budgery.api.domain.controller.record.TransactionDto
import io.budgery.api.domain.repository.TransactionRepository

class TransactionService(private val transactionRepository: TransactionRepository) {

    fun getTransaction(userId: Int, transactionId: Int): TransactionDto {
        return transactionRepository.findOne(userId, transactionId)?.let {
            TransactionDto(it)
        } ?: throw NoSuchElementException("No such transaction was found")
    }

    fun getTransactions(userId: Int): List<TransactionDto> {
        return transactionRepository.findAll(userId).map { TransactionDto(it) }
    }

    fun addTransaction(userId: Int, reqObj: TransactionCreateDto): TransactionDto {
        val newTransaction = transactionRepository.create(userId, reqObj)
        return TransactionDto(newTransaction)
    }

    fun deleteTransaction(userId: Int, transactionId: Int) : Boolean {
        return transactionRepository.deleteOne(userId, transactionId)
    }
}