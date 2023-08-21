package org.expenny.service.routes

import org.expenny.service.auth.authentication.UserRole
import org.expenny.service.auth.authorization.authorize
import org.expenny.service.domain.controller.user.UserController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.users(userController: UserController) {
    route("/users") {
        post("/sign-in") { userController.signIn(this.context) }
        post("/sign-up") { userController.signUp(this.context) }

        authenticate {
            route("/{userId}") {
                get { userController.getUser(this.context) }

                authorize(UserRole.SUPER_USER) {
                    put { userController.updateUser(this.context) }
                    delete { userController.deleteUser(this.context) }

                    delete("/data") { userController.deleteUserData(this.context) }
                }
            }
        }
    }
}