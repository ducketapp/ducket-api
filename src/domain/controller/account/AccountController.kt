package io.ducket.api.domain.controller.account

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.ImportService
import io.ducket.api.domain.service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class AccountController(
    private val accountService: AccountService,
    private val importService: ImportService,
) {

    suspend fun getAccountDetails(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        val account = accountService.getAccountDetailsAccessibleToUser(userId, accountId)
        ctx.respond(HttpStatusCode.OK, account)
    }

    suspend fun getAccounts(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val allAccounts = accountService.getAccountsAccessibleToUser(userId)

        ctx.respond(HttpStatusCode.OK, allAccounts)
    }

    suspend fun createAccount(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<AccountCreateDto>().apply {
            accountService.createAccount(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun updateAccount(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        ctx.receive<AccountUpdateDto>().apply {
            accountService.updateAccount(userId, accountId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteAccount(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        if (accountService.deleteAccount(userId, accountId)) ctx.respond(HttpStatusCode.NoContent)
        else ctx.respond(HttpStatusCode.UnprocessableEntity)
    }

    suspend fun importAccountTransactions(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        val payloadMultiparts = ctx.receiveMultipart().readAllParts()

        importService.importAccountTransactions(userId, accountId,  payloadMultiparts).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }
}