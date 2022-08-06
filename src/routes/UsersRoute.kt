package dev.ducketapp.service.routes

import dev.ducketapp.service.auth.authentication.UserRole
import dev.ducketapp.service.auth.authorization.authorize
import dev.ducketapp.service.domain.controller.user.UserController
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