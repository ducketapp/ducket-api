package dev.ducket.api.routes

import dev.ducket.api.auth.authentication.UserRole
import dev.ducket.api.auth.authorization.authorize
import dev.ducket.api.domain.controller.user.UserController
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