package io.budgery.api.route

import io.budgery.api.AuthorizationException
import io.budgery.api.config.JwtConfig
import io.budgery.api.config.UserPrincipal
import io.budgery.api.domain.controller.account.AccountController
import io.budgery.api.domain.controller.budget.BudgetController
import io.budgery.api.domain.controller.category.CategoryController
import io.budgery.api.domain.controller.label.LabelController
import io.budgery.api.domain.controller.record.RecordController
import io.budgery.api.domain.controller.user.UserController
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Routing.users(userController: UserController) {
    route("/users") {
        post { userController.signUp(this.context) }
        post("/auth") { userController.signIn(this.context) }

        authenticate {
            route("{userId}") {
                // verify user access before proceeding the routes handling
                intercept(ApplicationCallPipeline.Call) {
                    call.parameters["userId"]?.let {
                        val jwtUserId = call.authentication.principal<UserPrincipal>()?.id
                        if (jwtUserId == null || it.toInt() != jwtUserId) { throw AuthorizationException() }
                    }
                }

                get { userController.getUserDetails(this.context) }
                put { userController.updateUserInfo(this.context) }
                delete { userController.deleteUserProfile(this.context) }
                // delete { userController.clearUserInfo(this.context) }

                route("images") {
                    route("upload") {
                        post { userController.uploadUserImage(this.context) }
                    }
                }
            }
        }
    }
}

fun Routing.accounts(accountController: AccountController) {
    authenticate {
        route("/accounts") {
            post { accountController.createUserAccount(this.context) }
            get { accountController.getUserAccounts(this.context) }
            // patch { accountController.deleteUserAccounts(this.context) }

            route("{accountId}") {
                get { accountController.getUserAccount(this.context) }
                put { accountController.updateUserAccount(this.context) }
                delete { accountController.deleteUserAccount(this.context) }
            }
        }
    }
}

fun Routing.categories(categoryController: CategoryController) {
    authenticate {
        route("/categories") {
            get { categoryController.getCategories(this.context) }

            route("{categoryId}") {
                get { categoryController.getCategory(this.context) }
            }
        }
    }
}

fun Routing.transactions(recordController: RecordController) {
    authenticate {
        route("/records") {
            get { recordController.getUserRecords(this.context) }
            route("/transactions") {
                post { recordController.addManualTransaction(this.context) }

                route("{transactionId}") {
                    get { recordController.getTransaction(this.context) }
                    delete { recordController.deleteTransaction(this.context) }
                }
            }
            route("/transfers") {
                post { recordController.addManualTransfer(this.context, JwtConfig.getPrincipal(this.context.authentication)) }

                route("{transferId}") {
                    delete { recordController.deleteTransfer(this.context) }
                }
            }
        }
    }
}

fun Routing.labels(labelController: LabelController) {
    authenticate {
        route("/labels") {
            post { labelController.createUserLabel(this.context) }
            get { labelController.getUserLabels(this.context) }
        }
    }
}

fun Routing.budgets(budgetController: BudgetController) {
    authenticate {
        route("/budgets") {
            get { budgetController.getUserBudgets(this.context) }
        }
    }
}

