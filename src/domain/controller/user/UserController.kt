package dev.ducketapp.service.domain.controller.user

import dev.ducketapp.service.auth.authentication.JwtManager
import dev.ducketapp.service.auth.authentication.UserPrincipal
import dev.ducketapp.service.auth.authentication.UserRole
import dev.ducketapp.service.domain.controller.user.dto.UserAuthenticateDto
import dev.ducketapp.service.domain.controller.user.dto.UserCreateDto
import dev.ducketapp.service.domain.controller.user.dto.UserUpdateDto
import dev.ducketapp.service.domain.service.*
import dev.ducketapp.service.principalOrThrow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.util.*
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
        ctx.respond(HttpStatusCode.OK)
    }

    suspend fun deleteUserData(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        userService.deleteUserData(userId)
        ctx.respond(HttpStatusCode.NoContent)
    }
}