package dev.ducket.api.routes

import dev.ducket.api.auth.authentication.UserRole
import dev.ducket.api.auth.authorization.authorize
import dev.ducket.api.domain.controller.periodic_budget.PeriodicBudgetController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.periodicBudgets(periodicBudgetController: PeriodicBudgetController) {
    authenticate {
        route("/periodic-budgets") {
            get { periodicBudgetController.getBudgets(this.context) }

            authorize(UserRole.SUPER_USER) {
                post { periodicBudgetController.createBudget(this.context) }
                delete { periodicBudgetController.deleteBudgets(this.context) }
            }

            route("/{budgetId}") {
                get { periodicBudgetController.getBudget(this.context) }

                authorize(UserRole.SUPER_USER) {
                    put { periodicBudgetController.updateBudget(this.context) }
                    delete { periodicBudgetController.deleteBudget(this.context) }
                }

                route("/limits") {
                    get { periodicBudgetController.getBudgetLimits(this.context) }

                    authorize(UserRole.SUPER_USER) {
                        post { periodicBudgetController.createBudgetLimit(this.context) }
                    }

                    route("/{limitId}") {
                        get { periodicBudgetController.getBudgetLimit(this.context) }

                        authorize(UserRole.SUPER_USER) {
                            post { periodicBudgetController.updateBudgetLimit(this.context) }
                        }
                    }
                }
            }
        }
    }
}
