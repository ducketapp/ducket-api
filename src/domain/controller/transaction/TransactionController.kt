package io.ducket.api.domain.controller.transaction

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.TransactionService
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
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transactionId = ctx.parameters.getOrFail("transactionId")

        transactionService.getTransaction(userId, transactionId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun addTransaction(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        ctx.receive<TransactionCreateDto>().apply {
            transactionService.addTransaction(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun deleteTransaction(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transactionId = ctx.parameters.getOrFail("transactionId")

        transactionService.deleteTransaction(userId, transactionId).apply {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }

    suspend fun deleteTransactions(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        ctx.receive<TransactionDeleteDto>().apply {
            transactionService.deleteTransactions(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }

    suspend fun uploadTransactionAttachments(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transactionId = ctx.parameters.getOrFail("transactionId")

        transactionService.addAttachments(userId, transactionId, ctx.receiveMultipart().readAllParts()).apply {
            transactionService.getTransaction(userId, transactionId).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun downloadTransactionAttachment(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transactionId = ctx.parameters.getOrFail("transactionId")
        val attachmentId = ctx.parameters.getOrFail("attachmentId")

        transactionService.getAttachmentFile(userId, transactionId, attachmentId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }

    suspend fun deleteTransactionAttachment(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transactionId = ctx.parameters.getOrFail("transactionId")
        val attachmentId = ctx.parameters.getOrFail("attachmentId")

        transactionService.deleteAttachment(userId, transactionId, attachmentId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }
}