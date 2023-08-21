package org.expenny.service.routes

import org.expenny.service.auth.authentication.UserRole
import org.expenny.service.auth.authorization.authorize
import org.expenny.service.domain.controller.budget.BudgetController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

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