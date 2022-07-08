package io.ducket.api.routes

import io.ducket.api.auth.UserRole
import io.ducket.api.auth.authorization.authorize
import io.ducket.api.domain.controller.budget.BudgetController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.budgets(budgetController: BudgetController) {
    authenticate {
        route("/budgets") {
            get { budgetController.getBudgets(this.context) }

            authorize(UserRole.SUPER_USER) {
                post { budgetController.createBudget(this.context) }
                delete { budgetController.deleteBudgets(this.context) }
            }

            route("/{budgetId}") {
                get { budgetController.getBudget(this.context) }

                authorize(UserRole.SUPER_USER) {
                    put { budgetController.updateBudget(this.context) }
                    delete { budgetController.deleteBudget(this.context) }
                }
            }
        }
    }
}