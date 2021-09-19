package io.budgery.api.route

import io.budgery.api.AuthorizationException
import io.budgery.api.config.JwtConfig
import io.budgery.api.config.UserPrincipal
import io.budgery.api.domain.controller.account.AccountController
import io.budgery.api.domain.controller.budget.BudgetController
import io.budgery.api.domain.controller.category.CategoryController
import io.budgery.api.domain.controller.label.LabelController
import io.budgery.api.domain.controller.record.RecordController
import io.budgery.api.domain.controller.transaction.TransactionController
import io.budgery.api.domain.controller.transfer.TransferController
import io.budgery.api.domain.controller.user.UserController
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Routing.users(userController: UserController) {
    route("/users") {
        post { userController.signUp(this.context) }
        post("/auth") { userController.signIn(this.context) }

        authenticate {
            route("/{userId}") {
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

                route("/attachments") {
                    post { userController.uploadUserImage(this.context) }
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
            // TODO add delete

            route("/{accountId}") {
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

            route("/{categoryId}") {
                get { categoryController.getCategory(this.context) }
            }
        }
    }
}

fun Routing.records(recordController: RecordController) {
    authenticate {
        route("/records") {
            get { recordController.getUserRecords(this.context) }
        }
    }
}

fun Routing.transactions(transactionController: TransactionController) {
    authenticate {
        route("/transactions") {
            post { transactionController.addTransaction(this.context) }

            route("/{transactionId}") {
                get { transactionController.getTransaction(this.context) }
                delete { transactionController.deleteTransaction(this.context) }
                // TODO add delete

                route("/attachments") {
                    post { transactionController.addTransactionAttachments(this.context) }
                    // TODO add delete

                    route("/{attachmentId}") {
                        get { transactionController.getTransactionAttachment(this.context) }
                    }
                }
            }
        }
    }
}

fun Routing.transfers(transferController: TransferController) {
    authenticate {
        route("/transfers") {
            post { transferController.addTransfer(this.context) }

            route("/{transferId}") {
                delete { transferController.deleteTransfer(this.context) }
                // TODO add delete

                route("/attachments") {
                    post { transferController.addTransferAttachments(this.context) }
                    // TODO add delete

                    route("/{attachmentId}") {
                        get { transferController.getTransferAttachment(this.context) }
                    }
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
            post { budgetController.createCategoryBudget(this.context) }
            get { budgetController.getUserBudgets(this.context) }

            route("/{id}") {
                //get { budgetController.getUserBudgets(this.context) }
            }
        }
    }
}

