package dev.ducket.api.routes


import dev.ducket.api.auth.authentication.UserRole
import dev.ducket.api.auth.authorization.authorize
import dev.ducket.api.domain.controller.account.AccountController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

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