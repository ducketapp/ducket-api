package io.ducket.api.domain.controller.rule

import io.ducket.api.domain.service.ImportRuleService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class ImportRuleController(
    private val importRuleService: ImportRuleService,
) {

    suspend fun createImportRule(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<ImportRuleCreateDto>().apply {
            importRuleService.createImportRule(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getImportRules(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        importRuleService.getImportRules(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getImportRule(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importRuleId = ctx.parameters.getOrFail("ruleId").toLong()

        importRuleService.getImportRule(userId, importRuleId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun updateImportRule(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importRuleId = ctx.parameters.getOrFail("ruleId").toLong()

        ctx.receive<ImportRuleUpdateDto>().apply {
            importRuleService.updateImportRule(userId, importRuleId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteImportRule(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importRuleId = ctx.parameters.getOrFail("ruleId").toLong()

        importRuleService.deleteImportRule(userId, importRuleId).apply {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }
}