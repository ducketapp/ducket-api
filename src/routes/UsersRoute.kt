package io.ducket.api.routes

import io.ducket.api.config.UserPrincipal
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.plugins.AuthorizationException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

// TODO add deleteUserData
fun Route.users(userController: UserController) {
    route("/users") {
        post("/auth") { userController.signIn(this.context) }
        post { userController.signUp(this.context) }

        authenticate {
            route("/{userId}") {
                // verify user access before proceeding the routes handling
                intercept(ApplicationCallPipeline.Call) {
                    call.parameters["userId"]?.let { userId ->
                        call.authentication.principal<UserPrincipal>()?.id?.takeIf { it == userId }
                            ?: throw AuthorizationException()
                    }
                }

                get { userController.getUser(this.context) }
                put { userController.updateUser(this.context) }
                delete { userController.deleteUser(this.context) }

                route("/following") {
                    get { userController.getUserFollowing(this.context) }
                    post { userController.createUserFollowRequest(this.context) }

                    route("/{followId}") {
                        delete { userController.unfollowUser(this.context) }
                    }
                }

                route("/followers") {
                    get { userController.getUserFollowers(this.context) }

                    route("/{followId}") {
                        post { userController.approveUserFollowRequest(this.context) }
                        // delete { userController.removeUserFollower(this.context) }
                    }
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