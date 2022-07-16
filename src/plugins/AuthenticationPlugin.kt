package dev.ducket.api.plugins

import dev.ducket.api.auth.authentication.JwtManager
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.installAuthenticationPlugin() {
    val jwtManager by inject<JwtManager>()

    install(Authentication) {
        jwt {
            verifier(jwtManager.verifier)
            // what to do when a non-authenticated request is made to a protected URL
            challenge { _, _ -> throw AuthenticationException("Invalid auth token") }
            // defines how to extract a Principal from a session
            validate {
                jwtManager.validateToken(jwtCredential = it)
            }
        }
    }
}