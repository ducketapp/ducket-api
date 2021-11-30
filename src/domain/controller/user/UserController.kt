package io.ducket.api.domain.controller.user

import io.ducket.api.config.JwtConfig
import io.ducket.api.config.UserPrincipal
import io.ducket.api.domain.controller.follow.FollowUserDto
import io.ducket.api.domain.service.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class UserController(
    private val userService: UserService,
    private val accountService: AccountService,
    private val budgetService: BudgetService,
) {

    suspend fun signUp(ctx: ApplicationCall) {
        ctx.receive<UserSignUpDto>().apply {
            userService.signUp(this.validate()).apply {
                ctx.response.header(HttpHeaders.Authorization, JwtConfig.generateToken(UserPrincipal(this.user)))
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun signIn(ctx: ApplicationCall) {
        ctx.receive<UserSignInDto>().apply {
            userService.signIn(this.validate()).apply {
                ctx.response.header(HttpHeaders.Authorization, JwtConfig.generateToken(UserPrincipal(this.user)))
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun getUser(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        userService.getUser(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun updateUser(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<UserUpdateDto>().apply {
            userService.updateUser(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun uploadUserImage(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        userService.uploadUserImage(userId, ctx.receiveMultipart().readAllParts())

        userService.getUser(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun downloadUserImage(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val imageId = ctx.parameters.getOrFail("imageId")

        userService.downloadUserImage(userId, imageId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }

    suspend fun deleteUserImage(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val imageId = ctx.parameters.getOrFail("imageId")

        userService.deleteUserImage(userId, imageId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }

    suspend fun deleteUser(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        userService.deleteUser(userId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }

    suspend fun getUserFollowing(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        userService.getUserFollowing(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getUserFollowers(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        userService.getUserFollowers(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun createUserFollowRequest(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<FollowUserDto>().apply {
            userService.createUserFollowRequest(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun approveUserFollowRequest(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val followRequestId = ctx.parameters.getOrFail("followId")

        userService.approveUserFollowRequest(userId, followRequestId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun unfollowUser(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val followId = ctx.parameters.getOrFail("followId")

        userService.unfollowUser(userId, followId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }
}