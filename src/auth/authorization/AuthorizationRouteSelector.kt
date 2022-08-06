package dev.ducketapp.service.auth.authorization

import io.ktor.server.routing.*

class AuthorizationRouteSelector(private val description: String) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Constant
    override fun toString() = "(authorize ${description})"
}