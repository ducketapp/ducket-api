package io.ducket.api.auth.authorization

import io.ktor.routing.*

class RoleBasedAuthorizationRouteSelector(private val description: String) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Constant
    }

    override fun toString(): String {
        return "(authorize ${description})"
    }
}