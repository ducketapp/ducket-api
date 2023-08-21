package org.expenny.service.domain.controller.budget

import org.expenny.service.domain.controller.BulkDeleteDto
import org.expenny.service.domain.controller.budget.dto.BudgetCreateDto
import org.expenny.service.domain.controller.budget.dto.BudgetUpdateDto
import org.expenny.service.domain.service.BudgetService
import org.expenny.service.principalOrThrow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.util.*

class BudgetController(private val budgetService: BudgetService) {

    suspend fun createBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BudgetCreateDto>().let { reqObj ->
            budgetService.createBudget(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun updateBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        ctx.receive<BudgetUpdateDto>().let { reqObj ->
            budgetService.updateBudget(userId, budgetId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun getBudgets(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        budgetService.getBudgets(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun getBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        budgetService.getBudget(userId, budgetId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun deleteBudgets(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().let { reqObj ->
            budgetService.deleteBudgets(userId, reqObj.validate()).let {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }

    suspend fun deleteBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        budgetService.deleteBudget(userId, budgetId).let {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }
}