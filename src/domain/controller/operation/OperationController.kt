package io.ducket.api.domain.controller.operation

import io.ducket.api.domain.controller.operation.dto.OperationCreateUpdateDto
import io.ducket.api.domain.service.OperationService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class OperationController(private val operationService: OperationService) {

    suspend fun createOperation(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<OperationCreateUpdateDto>().let { reqObj ->
            operationService.createOperation(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun updateOperation(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val operationId = ctx.parameters.getOrFail("operationId").toLong()

        ctx.receive<OperationCreateUpdateDto>().let { reqObj ->
            operationService.updateOperation(userId, operationId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun getOperations(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        operationService.getOperations(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun getOperation(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val operationId = ctx.parameters.getOrFail("operationId").toLong()

        operationService.getOperation(userId, operationId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun deleteOperation(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val operationId = ctx.parameters.getOrFail("operationId").toLong()

        operationService.deleteOperation(userId, operationId).let {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }
}