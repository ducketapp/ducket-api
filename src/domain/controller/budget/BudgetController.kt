package io.ducket.api.domain.controller.budget

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.BudgetService
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
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<BudgetCreateDto>().apply {
            budgetService.createBudget(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getBudgets(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        budgetService.getBudgets(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getBudgetDetails(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val budgetId = ctx.parameters.getOrFail("budgetId")

        budgetService.getBudgetDetails(userId, budgetId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun deleteBudget(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val budgetId = ctx.parameters.getOrFail("budgetId")

        budgetService.deleteBudget(userId, budgetId).apply {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }
}