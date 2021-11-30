package io.ducket.api.routes

import io.ducket.api.domain.controller.budget.BudgetController
import io.ktor.auth.*
import io.ktor.routing.*

// TODO add delete multiple
fun Route.budgets(budgetController: BudgetController) {
    authenticate {
        route("/budgets") {
            post { budgetController.createBudget(this.context) }
            get { budgetController.getBudgets(this.context) }

            route("/{budgetId}") {
                get { budgetController.getBudgetDetails(this.context) }
                delete { budgetController.deleteBudget(this.context) }
            }
        }
    }
}