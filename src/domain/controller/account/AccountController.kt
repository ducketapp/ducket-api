package dev.ducket.api.domain.controller.account

import dev.ducket.api.domain.controller.BulkDeleteDto
import dev.ducket.api.domain.controller.account.dto.AccountCreateDto
import dev.ducket.api.domain.controller.account.dto.AccountUpdateDto
import dev.ducket.api.domain.service.AccountService
import dev.ducket.api.domain.service.ImportService
import dev.ducket.api.principalOrThrow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.util.*

class AccountController(private val accountService: AccountService) {

    suspend fun getAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        accountService.getAccount(userId, accountId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun getAccounts(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        accountService.getAccounts(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun createAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<AccountCreateDto>().let { reqObj ->
            accountService.createAccount(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun updateAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        ctx.receive<AccountUpdateDto>().let { reqObj ->
            accountService.updateAccount(userId, accountId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun deleteAccounts(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().let { reqObj ->
            accountService.deleteAccounts(userId, reqObj.validate())
            ctx.respond(HttpStatusCode.NoContent)
        }
    }

    suspend fun deleteAccount(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val accountId = ctx.parameters.getOrFail("accountId").toLong()

        accountService.deleteAccount(userId, accountId)
        ctx.respond(HttpStatusCode.NoContent)
    }
}