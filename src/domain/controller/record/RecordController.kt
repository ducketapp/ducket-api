package io.ducket.api.domain.controller.record

import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.TransactionService
import io.ducket.api.domain.service.TransferService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*

class RecordController(
    val transactionService: TransactionService,
    val transferService: TransferService,
    val accountService: AccountService,
) {

    suspend fun getRecords(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        val allRecords = listOf(
            transactionService.getTransactionsAccessibleToUser(userId),
            transferService.getTransfersAccessibleToUser(userId),
        ).flatten().sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.id })

        ctx.respond(HttpStatusCode.OK, allRecords)
    }
}