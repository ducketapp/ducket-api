package io.ducket.api.auth

import io.ducket.api.plugins.AuthorizationException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class Authorization(configuration: Configuration) {
    private val getRoles = configuration._getRoles

    class Configuration {
        internal var _getRoles: (Principal) -> Set<UserRole> = { emptySet() }

        fun getRoles(principal: (Principal) -> Set<UserRole>) {
            _getRoles = principal
        }
    }

    fun interceptPipeline(
        pipeline: ApplicationCallPipeline,
        roles: Set<UserRole>,
    ) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.ChallengePhase)
        pipeline.insertPhaseAfter(Authentication.ChallengePhase, AuthorizePhase)

        pipeline.intercept(AuthorizePhase) {
            val principal = call.authentication.principal<Principal>() ?: throw AuthorizationException("Undefined principal")
            val acceptableRoles = getRoles(principal)

            if (roles.none { it in acceptableRoles }) {
                throw AuthorizationException()
            }
        }
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Authorization> {
        override val key = AttributeKey<Authorization>("Authorization")

        val AuthorizePhase = PipelinePhase("Authorize")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Authorization {
            return Authorization(Configuration().apply(configure))
        }
    }
}

class AuthorizationRouteSelector(private val description: String) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Constant
    }

    override fun toString(): String {
        return "(authorize ${description})"
    }
}

fun Route.authorize(vararg roles: UserRole, build: Route.() -> Unit): Route {
    val description = roles.let { "anyOf (${roles.joinToString(" ")})" }
    val authorizationRoute = createChild(AuthorizationRouteSelector(description))

    application.feature(Authorization).interceptPipeline(authorizationRoute, roles.toSet())
    authorizationRoute.build()

    return authorizationRoute
}