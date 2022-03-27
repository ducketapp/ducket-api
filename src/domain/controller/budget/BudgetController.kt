package io.ducket.api.domain.controller.budget

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.service.BudgetService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*


class BudgetController(
    private val budgetService: BudgetService
) {

    suspend fun createBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BudgetCreateDto>().apply {
            budgetService.createBudget(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getBudgets(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        budgetService.getBudgetsAccessibleToUser(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        budgetService.getBudgetAccessibleToUser(userId, budgetId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun deleteBudgets(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().apply {
            budgetService.deleteBudgets(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }

    suspend fun deleteBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        budgetService.deleteBudget(userId, budgetId).apply {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }
}