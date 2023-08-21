package org.expenny.service.routes


import org.expenny.service.auth.authentication.UserRole
import org.expenny.service.auth.authorization.authorize
import org.expenny.service.domain.controller.account.AccountController
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