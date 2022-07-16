package dev.ducket.api.auth.authorization

import dev.ducket.api.auth.authentication.UserRole
import dev.ducket.api.plugins.AuthorizationException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.authorize(vararg roles: UserRole, build: Route.() -> Unit): Route {
    val description = roles.let { "anyOf (${roles.joinToString(" ")})" }
    val authorizationRoute = createChild(AuthorizationRouteSelector(description))

    authorizationRoute.install(AuthorizationInterceptor) {
        acceptableRoles = roles.toSet()
    }
    authorizationRoute.build()

    return authorizationRoute
}

val AuthorizationInterceptor = createRouteScopedPlugin("AuthorizationInterceptor", ::PluginConfiguration) {
    val acceptableRoles = pluginConfig.acceptableRoles

    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val authorizationConfig = call.application.plugin(Authorization).config

            val principal = call.authentication.principal<Principal>() ?: throw AuthorizationException("Missing authorization")
            val userRole = authorizationConfig.getRole(principal)
            if (userRole !in acceptableRoles) {
                throw AuthorizationException("You are not entitled to visit this resource")
            }
        }
    }
}

class PluginConfiguration {
    lateinit var acceptableRoles: Set<UserRole>
}
