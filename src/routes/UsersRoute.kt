package io.ducket.api.routes

import io.ducket.api.domain.controller.user.UserController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.users(
    userController: UserController,
) {
    route("/users") {
        route("/auth") {
            post("/sign-in") { userController.signIn(this.context) }
            post("/sign-up") { userController.signUp(this.context) }
        }

        authenticate {
            get { userController.getUsers(this.context) }

            route("/{userId}") {
                get { userController.getUser(this.context) }
                put { userController.updateUser(this.context) }
                delete { userController.deleteUser(this.context) }

                route("/data") {
                    delete { userController.deleteUserData(this.context) }
                }

                route("/follow") {
                    post { userController.followUser(this.context) }

                    route("/{followId}") {
                        post { userController.updateFollow(this.context) }
                    }
                }

                route("/following") {
                    get { userController.getUserFollowing(this.context) }
                }

                route("/followers") {
                    get { userController.getUserFollowers(this.context) }
                }

                route("/images") {
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