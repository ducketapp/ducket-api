package io.ducket.api.domain.controller.rule

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.rule.dto.ImportRuleCreateUpdateDto
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

        ctx.receive<ImportRuleCreateUpdateDto>().let { reqObj ->
            importRuleService.createImportRule(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun getImportRules(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        importRuleService.getImportRules(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun getImportRule(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importRuleId = ctx.parameters.getOrFail("importRuleId").toLong()

        importRuleService.getImportRule(userId, importRuleId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun updateImportRule(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importRuleId = ctx.parameters.getOrFail("importRuleId").toLong()

        ctx.receive<ImportRuleCreateUpdateDto>().let { reqObj ->
            importRuleService.updateImportRule(userId, importRuleId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun deleteImportRules(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().let { reqObj ->
            importRuleService.deleteImportRules(userId, reqObj.validate())
            ctx.respond(HttpStatusCode.NoContent)
        }
    }

    suspend fun deleteImportRule(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importRuleId = ctx.parameters.getOrFail("importRuleId").toLong()

        importRuleService.deleteImportRule(userId, importRuleId)
        ctx.respond(HttpStatusCode.NoContent)
    }
}