package io.ducket.api.domain.controller.account

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.ImportService
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

    suspend fun getUserAccount(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val accountId = ctx.parameters.getOrFail("accountId")

        val account = accountService.getAccount(userId, accountId)
        ctx.respond(HttpStatusCode.OK, account)
    }

    suspend fun getUserAccounts(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        val accounts = accountService.getAccounts(userId)
        ctx.respond(HttpStatusCode.OK, accounts)
    }

    suspend fun createUserAccount(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        ctx.receive<AccountCreateDto>().apply {
            accountService.createAccount(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun updateUserAccount(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val accountId = ctx.parameters.getOrFail("accountId")

        ctx.receive<AccountUpdateDto>().apply {
            accountService.updateAccount(userId, accountId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteUserAccount(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val accountId = ctx.parameters.getOrFail("accountId")

        if (accountService.deleteAccount(userId, accountId)) ctx.respond(HttpStatusCode.NoContent)
        else ctx.respond(HttpStatusCode.UnprocessableEntity)
    }

    suspend fun importTransactions(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val accountId = ctx.parameters.getOrFail("accountId")

        importService.importTransactions(userId, accountId,  ctx.receiveMultipart().readAllParts()).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

/*    suspend fun deleteUserAccounts(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipalId(ctx.authentication)

        ctx.receive<AccountDeleteDTO>().apply {
            accountService.deleteAccounts(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }*/
}