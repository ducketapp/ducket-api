package io.ducket.api.route

import io.ducket.api.AuthorizationException
import io.ducket.api.config.UserPrincipal
import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.label.LabelController
import io.ducket.api.domain.controller.record.RecordController
import io.ducket.api.domain.controller.transaction.TransactionController
import io.ducket.api.domain.controller.transfer.TransferController
import io.ducket.api.domain.controller.user.UserController
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
                    call.parameters["userId"]?.let { userId ->
                        call.authentication.principal<UserPrincipal>()?.id?.takeIf { jwtUserId ->
                            jwtUserId != null && jwtUserId == userId
                        } ?: throw AuthorizationException()
                    }
                }

                get { userController.getUserDetails(this.context) }
                put { userController.updateUserInfo(this.context) }
                delete { userController.deleteUserProfile(this.context) }
                // TODO add deleteUserInfo

                route("/image") {
                    post { userController.uploadUserImage(this.context) }

                    route("/{imageId}") {
                        get { userController.downloadUserImage(this.context) }
                        delete { userController.deleteUserImage(this.context) }
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
            // TODO add delete multiple

            route("/{accountId}") {
                get { accountController.getUserAccount(this.context) }
                put { accountController.updateUserAccount(this.context) }
                delete { accountController.deleteUserAccount(this.context) }

                route("/import") {
                    post { accountController.importTransactions(this.context) }
                }
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
            patch { transactionController.deleteTransactions(this.context) }

            route("/{transactionId}") {
                get { transactionController.getTransaction(this.context) }
                delete { transactionController.deleteTransaction(this.context) }

                route("/attachments") {
                    post { transactionController.uploadTransactionAttachments(this.context) }
                    // TODO add delete multiple

                    route("/{attachmentId}") {
                        get { transactionController.downloadTransactionAttachment(this.context) }
                        delete { transactionController.deleteTransactionAttachment(this.context) }
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
            // TODO add delete multiple

            route("/{transferId}") {
                delete { transferController.getTransfer(this.context) }
                delete { transferController.deleteTransfer(this.context) }

                route("/attachments") {
                    post { transferController.uploadTransferAttachments(this.context) }
                    // TODO add delete multiple

                    route("/{attachmentId}") {
                        get { transferController.downloadTransferAttachment(this.context) }
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

            route("/{budgetId}") {
                get { budgetController.getUserBudget(this.context) }
                delete { budgetController.deleteUserBudget(this.context) }
            }
        }
    }
}

