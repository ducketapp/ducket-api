package io.ducket.api.routes


import io.ducket.api.auth.UserRole
import io.ducket.api.auth.authorization.authorize
import io.ducket.api.domain.controller.account.AccountController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.accounts(accountController: AccountController) {
    authenticate {
        route("/accounts") {
            get { accountController.getAccounts(this.context) }

            authorize(UserRole.SUPER_USER) {
                post { accountController.createAccount(this.context) }
                delete { accountController.deleteAccounts(this.context) }
            }

            route("/{accountId}") {
                get { accountController.getAccount(this.context) }

                authorize(UserRole.SUPER_USER) {
                    put { accountController.updateAccount(this.context) }
                    delete { accountController.deleteAccount(this.context) }
                }
            }
        }
    }
}