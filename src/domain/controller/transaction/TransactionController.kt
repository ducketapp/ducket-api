package io.ducket.api.domain.controller.transaction

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.TransactionService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class TransactionController(
    val transactionService: TransactionService,
    val accountService: AccountService,
) {

    suspend fun getTransaction(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val transactionId = ctx.parameters.getOrFail("transactionId").toLong()

        transactionService.getTransactionAccessibleToUser(userId, transactionId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun createTransaction(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<TransactionCreateDto>().apply {
            transactionService.createTransaction(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun deleteTransaction(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val transactionId = ctx.parameters.getOrFail("transactionId").toLong()

        transactionService.deleteTransaction(userId, transactionId).apply {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }

    suspend fun deleteTransactions(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().apply {
            transactionService.deleteTransactions(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }

    suspend fun uploadTransactionAttachments(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val transactionId = ctx.parameters.getOrFail("transactionId").toLong()

        transactionService.uploadTransactionAttachments(userId, transactionId, ctx.receiveMultipart().readAllParts()).apply {
            transactionService.getTransactionAccessibleToUser(userId, transactionId).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun downloadTransactionAttachment(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val transactionId = ctx.parameters.getOrFail("transactionId").toLong()
        val attachmentId = ctx.parameters.getOrFail("imageId").toLong()
        val ownerId = ctx.request.queryParameters["ownerId"]?.toLong() ?: userId

        transactionService.downloadTransactionAttachment(ownerId, transactionId, attachmentId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }

    suspend fun deleteTransactionAttachment(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val transactionId = ctx.parameters.getOrFail("transactionId").toLong()
        val attachmentId = ctx.parameters.getOrFail("imageId").toLong()

        transactionService.deleteTransactionAttachment(userId, transactionId, attachmentId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }
}