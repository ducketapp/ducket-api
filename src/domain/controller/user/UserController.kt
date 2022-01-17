package io.ducket.api.domain.controller.user

import io.ducket.api.config.JwtManager
import io.ducket.api.config.UserPrincipal
import io.ducket.api.domain.service.*
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import org.koin.java.KoinJavaComponent.inject

class UserController(
    private val userService: UserService,
) {
    private val jwtManager: JwtManager by inject(JwtManager::class.java)

    suspend fun signUp(ctx: ApplicationCall) {
        ctx.receive<UserSignUpDto>().apply {
            userService.createUser(this.validate()).apply {
                ctx.response.header(
                    name = HttpHeaders.Authorization,
                    value = "Bearer ${jwtManager.generateToken(UserPrincipal(this.id, this.email))}",
                )
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun signIn(ctx: ApplicationCall) {
        ctx.receive<UserSignInDto>().apply {
            userService.getUser(this.validate()).apply {
                ctx.response.header(
                    name = HttpHeaders.Authorization,
                    value = "Bearer ${jwtManager.generateToken(UserPrincipal(this.id, this.email))}",
                )
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun getUsers(ctx: ApplicationCall) {
        userService.getUsers().apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getUser(ctx: ApplicationCall) {
        val userId = ctx.parameters.getOrFail("userId").toLong()

        userService.getUser(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun updateUser(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<UserUpdateDto>().apply {
            userService.updateUser(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteUser(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        userService.deleteUser(userId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }

    suspend fun deleteUserData(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        userService.deleteUserData(userId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }

    suspend fun getUserFollowing(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        userService.getUserFollowing(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getUserFollowers(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        userService.getUserFollowers(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun followUser(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val userToFollowId = ctx.request.queryParameters.getOrFail("targetUserId").toLong()

        userService.createUserFollowRequest(userId, userToFollowId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun updateFollow(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val followRequestId = ctx.parameters.getOrFail("followId").toLong()
        val action = ctx.request.queryParameters.getOrFail("action")

        userService.updateUserFollow(userId, followRequestId, action).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }
}