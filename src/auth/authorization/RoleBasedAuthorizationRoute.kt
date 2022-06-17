package io.ducket.api.auth.authorization

import io.ducket.api.auth.UserRole
import io.ktor.application.*
import io.ktor.routing.*


fun Route.authorize(vararg roles: UserRole, build: Route.() -> Unit): Route {
    val description = roles.let { "anyOf (${roles.joinToString(" ")})" }
    val authorizationRoute = createChild(RoleBasedAuthorizationRouteSelector(description))

    application.feature(RoleBasedAuthorization).interceptPipeline(authorizationRoute, roles.toSet())
    authorizationRoute.build()

    return authorizationRoute
}