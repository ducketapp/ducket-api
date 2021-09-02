package io.budgery.api.domain.controller.budget

import io.budgery.api.config.JwtConfig
import io.budgery.api.domain.service.BudgetService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*


class BudgetController(
    private val budgetService: BudgetService
) {

    suspend fun getUserBudgets(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        val budgets = budgetService.getBudgets(userId)
        ctx.respond(HttpStatusCode.OK, budgets)
    }
}