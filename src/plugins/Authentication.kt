package io.ducket.api.plugins

import io.ducket.api.auth.JwtManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.installAuthentication() {
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