package io.ducket.api.domain.controller.transfer

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.TransferService
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
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        ctx.receive<TransferCreateDto>().apply {
            transferService.addTransfer(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getTransfer(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transferId = ctx.parameters.getOrFail("transferId")

        transferService.getTransfer(userId, transferId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun deleteTransfer(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transferId = ctx.parameters.getOrFail("transferId")

        transferService.deleteTransfer(userId, transferId).apply {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }

    suspend fun uploadTransferAttachments(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transferId = ctx.parameters.getOrFail("transferId")

        transferService.addAttachments(userId, transferId, ctx.receiveMultipart().readAllParts()).apply {
            transferService.getTransfer(userId, transferId).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun downloadTransferAttachment(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val transferId = ctx.parameters.getOrFail("transferId")
        val attachmentId = ctx.parameters.getOrFail("attachmentId")

        transferService.getAttachmentFile(userId, transferId, attachmentId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }
}