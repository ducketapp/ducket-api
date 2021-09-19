package io.budgery.api.domain.controller.transaction

import io.budgery.api.config.JwtConfig
import io.budgery.api.domain.service.AccountService
import io.budgery.api.domain.service.AttachmentService
import io.budgery.api.domain.service.TransactionService
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
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transactionId = ctx.parameters.getOrFail("transactionId").toInt()

        val account = transactionService.getTransaction(userId, transactionId)
        ctx.respond(HttpStatusCode.OK, account)
    }

    suspend fun addTransaction(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<TransactionCreateDto>().apply {
            transactionService.addTransaction(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun deleteTransaction(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transactionId = ctx.parameters.getOrFail("transactionId").toInt()

        if (transactionService.deleteTransaction(userId, transactionId))
            ctx.respond(HttpStatusCode.NoContent)
        else
            ctx.respond(HttpStatusCode.UnprocessableEntity)
    }

    suspend fun addTransactionAttachments(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transactionId = ctx.parameters.getOrFail("transactionId").toInt()

        transactionService.addAttachments(userId, transactionId, ctx.receiveMultipart().readAllParts())

        transactionService.getTransaction(userId, transactionId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getTransactionAttachment(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transactionId = ctx.parameters.getOrFail("transactionId").toInt()
        val attachmentId = ctx.parameters.getOrFail("attachmentId").toInt()

        transactionService.getAttachmentFile(userId, transactionId, attachmentId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }
}