package io.ducket.api.domain.controller.account

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.ImportService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class AccountController(
    private val accountService: AccountService,
    private val importService: ImportService,
) {

    suspend fun getAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        val account = accountService.getAccount(userId, accountId)
        ctx.respond(HttpStatusCode.OK, account)
    }

    suspend fun getAccounts(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val accounts = accountService.getAccounts(userId)

        ctx.respond(HttpStatusCode.OK, accounts)
    }

    suspend fun createAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<AccountCreateDto>().apply {
            accountService.createAccount(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun updateAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        ctx.receive<AccountUpdateDto>().apply {
            accountService.updateAccount(userId, accountId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteAccounts(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().apply {
            accountService.deleteAccounts(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }

    suspend fun deleteAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        accountService.deleteAccount(userId, accountId)
        ctx.respond(HttpStatusCode.NoContent)
    }

//    suspend fun importAccountTransactions(ctx: ApplicationCall) {
//        val userId = ctx.authentication.principalOrThrow().id
//        val accountId = ctx.parameters.getOrFail("accountId").toLong()
//
//        val payloadMultiparts = ctx.receiveMultipart().readAllParts()
//
//        importService.importAccountLedgerRecords(userId, accountId,  payloadMultiparts).apply {
//            ctx.respond(HttpStatusCode.OK, this)
//        }
//    }
}