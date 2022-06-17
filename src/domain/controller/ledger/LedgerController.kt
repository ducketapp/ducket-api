package io.ducket.api.domain.controller.ledger

import io.ducket.api.domain.service.LedgerService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class LedgerController(
    private val ledgerService: LedgerService,
) {

    suspend fun getLedgerRecords(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val ledgerRecords = ledgerService.getLedgerRecords(userId)

        ctx.respond(HttpStatusCode.OK, ledgerRecords)
    }

    suspend fun getLedgerRecord(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val recordId = ctx.parameters.getOrFail("recordId").toLong()

        ledgerService.getLedgerRecord(userId, recordId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun deleteLedgerRecord(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val recordId = ctx.parameters.getOrFail("recordId").toLong()

        ledgerService.deleteLedgerRecord(userId, recordId)

        ctx.respond(HttpStatusCode.NoContent)
    }

    suspend fun createLedgerRecord(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<LedgerRecordCreateDto>().apply {
            ledgerService.createLedgerRecord(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun uploadLedgerRecordAttachments(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val recordId = ctx.parameters.getOrFail("recordId").toLong()
        val operationId = ctx.parameters.getOrFail("operationId").toLong()

        ledgerService.uploadLedgerRecordAttachments(userId, recordId, operationId, ctx.receiveMultipart().readAllParts())

        ctx.respond(HttpStatusCode.NoContent)
    }

    suspend fun downloadLedgerRecordAttachment(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val recordId = ctx.parameters.getOrFail("recordId").toLong()
        val operationId = ctx.parameters.getOrFail("operationId").toLong()
        val imageId = ctx.parameters.getOrFail("imageId").toLong()

        ledgerService.downloadLedgerRecordAttachment(userId, recordId, operationId, imageId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }

    suspend fun deleteLedgerRecordAttachment(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val recordId = ctx.parameters.getOrFail("recordId").toLong()
        val operationId = ctx.parameters.getOrFail("operationId").toLong()
        val imageId = ctx.parameters.getOrFail("imageId").toLong()

        ledgerService.deleteLedgerRecordAttachment(userId, recordId, operationId, imageId)

        ctx.respond(HttpStatusCode.NoContent)
    }
}