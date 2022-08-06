package dev.ducketapp.service.routes


import dev.ducketapp.service.auth.authentication.UserRole
import dev.ducketapp.service.auth.authorization.authorize
import dev.ducketapp.service.domain.controller.account.AccountController
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