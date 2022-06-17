package io.ducket.api.auth.authorization

import io.ducket.api.auth.UserRole
import io.ducket.api.plugins.AuthorizationException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class RoleBasedAuthorization(configuration: Configuration) {
    private val getRole = configuration._getRole

    class Configuration {
        internal var _getRole: (Principal) -> UserRole? = { null }

        fun getRole(principal: (Principal) -> UserRole) {
            _getRole = principal
        }
    }

    fun interceptPipeline(
        pipeline: ApplicationCallPipeline,
        acceptableRoles: Set<UserRole>,
    ) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.ChallengePhase)
        pipeline.insertPhaseAfter(Authentication.ChallengePhase, AuthorizePhase)

        pipeline.intercept(AuthorizePhase) {
            val principal = call.authentication.principal<Principal>() ?: throw AuthorizationException("Undefined principal")
            val principalRole = getRole(principal)

            if (!acceptableRoles.contains(principalRole)) {
                throw AuthorizationException()
            }
        }
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RoleBasedAuthorization> {
        override val key = AttributeKey<RoleBasedAuthorization>("Authorization")

        val AuthorizePhase = PipelinePhase("Authorize")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RoleBasedAuthorization {
            return RoleBasedAuthorization(Configuration().apply(configure))
        }
    }
}