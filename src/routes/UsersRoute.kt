package io.ducket.api.routes

import io.ducket.api.auth.UserRole
import io.ducket.api.auth.authorization.authorize
import io.ducket.api.domain.controller.user.UserController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.users(userController: UserController) {
    route("/users") {
        post("/sign-in") { userController.signIn(this.context) }
        post("/sign-up") { userController.signUp(this.context) }

        authenticate {
            authorize(UserRole.SUPER_USER) {
                route("/{userId}") {
                    get { userController.getUser(this.context) }
                    put { userController.updateUser(this.context) }
                    delete { userController.deleteUser(this.context) }

                    delete("/data") { userController.deleteUserData(this.context) }
                }
            }
        }
    }
}