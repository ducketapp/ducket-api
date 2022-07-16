package dev.ducket.api.auth.authorization

import dev.ducket.api.auth.authentication.UserRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*

class AuthorizationConfiguration {
    lateinit var getRole : (principal: Principal) -> UserRole
}

class Authorization(val config: AuthorizationConfiguration) {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, AuthorizationConfiguration, Authorization> {
        override val key: AttributeKey<Authorization> = AttributeKey("Authorization")

        override fun install(pipeline: ApplicationCallPipeline, configure: AuthorizationConfiguration.() -> Unit): Authorization {
            return Authorization(AuthorizationConfiguration().apply(configure))
        }
    }
}
