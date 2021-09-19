package io.budgery.api.domain.controller.transfer

import io.budgery.api.config.JwtConfig
import io.budgery.api.config.UserPrincipal
import io.budgery.api.domain.service.AccountService
import io.budgery.api.domain.service.AttachmentService
import io.budgery.api.domain.service.TransferService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class TransferController(
    val transferService: TransferService,
    val accountService: AccountService,
) {

    suspend fun addTransfer(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<TransferCreateDto>().apply {
            transferService.addTransfer(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun deleteTransfer(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transferId = ctx.parameters.getOrFail("transferId").toInt()

        if (transferService.deleteTransfer(userId, transferId)) ctx.respond(HttpStatusCode.NoContent)
        else ctx.respond(HttpStatusCode.UnprocessableEntity)
    }

    suspend fun addTransferAttachments(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transferId = ctx.parameters.getOrFail("transferId").toInt()

        transferService.addAttachments(userId, transferId, ctx.receiveMultipart().readAllParts())

        transferService.getTransfer(userId, transferId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getTransferAttachment(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transferId = ctx.parameters.getOrFail("transferId").toInt()
        val attachmentId = ctx.parameters.getOrFail("attachmentId").toInt()

        transferService.getAttachmentFile(userId, transferId, attachmentId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }
}