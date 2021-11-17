package io.ducket.api.domain.controller.budget

import io.ducket.api.config.JwtConfig
import io.ducket.api.config.UserPrincipal
import io.ducket.api.domain.controller.transaction.TransactionDeleteDto
import io.ducket.api.domain.controller.user.UserSignUpDto
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

    suspend fun createCategoryBudget(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        ctx.receive<BudgetCreateDto>().apply {
            budgetService.createBudget(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getUserBudgets(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        budgetService.getBudgets(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getUserBudget(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val budgetId = ctx.parameters.getOrFail("budgetId")

        budgetService.getBudget(userId, budgetId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun deleteUserBudget(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val budgetId = ctx.parameters.getOrFail("budgetId")

        budgetService.deleteBudget(userId, budgetId).apply {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }
}