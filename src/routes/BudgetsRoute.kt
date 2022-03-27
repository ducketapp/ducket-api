package io.ducket.api.routes

import io.ducket.api.domain.controller.budget.BudgetController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.budgets(budgetController: BudgetController) {
    authenticate {
        route("/budgets") {
            post { budgetController.createBudget(this.context) }
            get { budgetController.getBudgets(this.context) }
            delete { budgetController.deleteBudgets(this.context) }

            route("/{budgetId}") {
                get { budgetController.getBudget(this.context) }
                // put { budgetController.updateBudget(this.context) }
                delete { budgetController.deleteBudget(this.context) }
            }
        }
    }
}