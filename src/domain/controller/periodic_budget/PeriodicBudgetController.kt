package  org.expenny.service.domain.controller.periodic_budget

import org.expenny.service.domain.controller.BulkDeleteDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetCreateDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitCreateDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetLimitUpdateDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetUpdateDto
import org.expenny.service.domain.service.PeriodicBudgetLimitService
import org.expenny.service.domain.service.PeriodicBudgetService
import org.expenny.service.principalOrThrow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.util.*


class PeriodicBudgetController(
    private val periodicBudgetService: PeriodicBudgetService,
    private val periodicBudgetLimitService: PeriodicBudgetLimitService,
) {

    suspend fun createBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<PeriodicBudgetCreateDto>().let { reqObj ->
            periodicBudgetService.createBudget(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun updateBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        ctx.receive<PeriodicBudgetUpdateDto>().let { reqObj ->
            periodicBudgetService.updateBudget(userId, budgetId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun getBudgets(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        periodicBudgetService.getBudgets(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun getBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        periodicBudgetService.getBudget(userId, budgetId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun deleteBudgets(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().let { reqObj ->
            periodicBudgetService.deleteBudgets(userId, reqObj.validate()).let {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }

    suspend fun deleteBudget(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        periodicBudgetService.deleteBudget(userId, budgetId).let {
            ctx.respond(HttpStatusCode.NoContent)
        }
    }

    suspend fun getBudgetLimits(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        periodicBudgetLimitService.getLimits(userId, budgetId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun getBudgetLimit(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()
        val limitId = ctx.parameters.getOrFail("limitId").toLong()

        periodicBudgetLimitService.getLimit(userId, budgetId, limitId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun createBudgetLimit(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()

        ctx.receive<PeriodicBudgetLimitCreateDto>().let { reqObj ->
            periodicBudgetLimitService.createLimit(userId, budgetId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun updateBudgetLimit(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val budgetId = ctx.parameters.getOrFail("budgetId").toLong()
        val limitId = ctx.parameters.getOrFail("limitId").toLong()

        ctx.receive<PeriodicBudgetLimitUpdateDto>().let { reqObj ->
            periodicBudgetLimitService.updateLimit(userId, budgetId, limitId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }
}