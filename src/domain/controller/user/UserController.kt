package io.ducket.api.domain.controller.user

import io.ducket.api.config.JwtConfig
import io.ducket.api.config.UserPrincipal
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
            userService.createUser(this.validate()).apply {
                ctx.response.header(HttpHeaders.Authorization, JwtConfig.generate(UserPrincipal(this.user)))
                ctx.respond(HttpStatusCode.Created, this)
            }

        }
    }

    suspend fun signIn(ctx: ApplicationCall) {
        ctx.receive<UserSignInDto>().apply {
            userService.authenticateUser(this.validate()).apply {
                ctx.response.header(HttpHeaders.Authorization, JwtConfig.generate(UserPrincipal(this.user)))
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun getUserDetails(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        val user = userService.getUser(userId)
        val userAccounts = accountService.getAccounts(userId)
        val userBudgets = budgetService.getBudgets(userId)
        // TODO discounts

        ctx.respond(HttpStatusCode.OK, UserDetailsDto(user, userAccounts, userBudgets))
    }

    suspend fun updateUserInfo(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        ctx.receive<UserUpdateDto>().apply {
            userService.updateUser(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun uploadUserImage(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        userService.addUserImage(userId, ctx.receiveMultipart().readAllParts())
        getUserDetails(ctx)
    }

    suspend fun downloadUserImage(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val imageId = ctx.parameters.getOrFail("imageId")

        userService.getUserImageFile(userId, imageId).apply {
            ctx.response.header("Content-Disposition", "attachment; filename=\"${this.name}\"")
            ctx.respondFile(this)
        }
    }

    suspend fun deleteUserImage(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()
        val imageId = ctx.parameters.getOrFail("imageId")

        userService.deleteUserImage(userId, imageId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }

    suspend fun deleteUserProfile(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        userService.deleteUser(userId).apply {
            if (this) ctx.respond(HttpStatusCode.NoContent)
            else ctx.respond(HttpStatusCode.UnprocessableEntity)
        }
    }
}