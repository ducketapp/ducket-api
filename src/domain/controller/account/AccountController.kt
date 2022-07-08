package io.ducket.api.domain.controller.account

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.account.dto.AccountUpdateDto
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