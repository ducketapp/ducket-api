package io.budgery.api.domain.controller.user

import io.budgery.api.config.JwtConfig
import io.budgery.api.config.UserPrincipal
import io.budgery.api.domain.service.AccountService
import io.budgery.api.domain.service.ImportService
import io.budgery.api.domain.service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import java.io.File

class UserController(
    private val userService: UserService,
    private val accountService: AccountService,
    private val importService: ImportService,
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
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        val user = userService.getUser(userId)
        val userAccounts = accountService.getAccounts(userId)
        val userImports = importService.getImports(userId)
        // TODO rules, budgets, goals, discounts

        ctx.respond(HttpStatusCode.OK, UserDetailsDto(user, userAccounts, userImports))
    }

    suspend fun updateUserInfo(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<UserUpdateDto>().apply {
            userService.updateUser(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun uploadUserImage(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        userService.uploadImage(userId, ctx.receiveMultipart()).apply {
            ctx.respond(HttpStatusCode.Created, object {
                val message = "User image was successfully uploaded!"
            })
        }
    }

    suspend fun deleteUserProfile(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        if (userService.deleteUser(userId)) ctx.respond(HttpStatusCode.NoContent)
        else ctx.respond(HttpStatusCode.UnprocessableEntity)
    }
}