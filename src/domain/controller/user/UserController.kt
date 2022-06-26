package io.ducket.api.domain.controller.user

import io.ducket.api.auth.JwtManager
import io.ducket.api.auth.UserPrincipal
import io.ducket.api.auth.UserRole
import io.ducket.api.domain.service.*
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import org.koin.java.KoinJavaComponent.inject

class UserController(private val userService: UserService) {
    private val jwtManager: JwtManager by inject(JwtManager::class.java)

    suspend fun signUp(ctx: ApplicationCall) {
        ctx.receive<UserCreateDto>().let { reqObj ->
            userService.createUser(reqObj.validate()).let { resObj ->
                ctx.response.header(
                    name = HttpHeaders.Authorization,
                    value = jwtManager.getAuthorizationHeaderValue(UserPrincipal(resObj.id, resObj.email, UserRole.SUPER_USER)),
                )
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun signIn(ctx: ApplicationCall) {
        ctx.receive<UserAuthenticateDto>().let { reqObj ->
            userService.authenticateUser(reqObj.validate()).let { resObj ->
                ctx.response.header(
                    name = HttpHeaders.Authorization,
                    value = jwtManager.getAuthorizationHeaderValue(UserPrincipal(resObj.id, resObj.email, UserRole.SUPER_USER)),
                )
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun getUser(ctx: ApplicationCall) {
        val userId = ctx.parameters.getOrFail("userId").toLong()

        userService.getUser(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun updateUser(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<UserUpdateDto>().let { reqObj ->
            userService.updateUser(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun deleteUser(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        userService.deleteUser(userId)
        ctx.respond(HttpStatusCode.NoContent)
    }

    suspend fun deleteUserData(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        userService.deleteUserData(userId)
        ctx.respond(HttpStatusCode.NoContent)
    }
}