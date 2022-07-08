package io.ducket.api.domain.controller.budget

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.budget.dto.BudgetCreateDto
import io.ducket.api.domain.controller.budget.dto.BudgetUpdateDto
import io.ducket.api.domain.service.BudgetService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

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