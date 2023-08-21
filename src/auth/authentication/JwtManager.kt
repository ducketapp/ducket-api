package org.expenny.service.auth.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.expenny.service.config.AppConfig
import org.expenny.service.domain.repository.UserRepository
import org.expenny.service.plugins.AuthenticationException
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.runBlocking

class JwtManager(appConfig: AppConfig) {
    private val jwtConfig = appConfig.jwtConfig
    private val algorithm = Algorithm.HMAC256(jwtConfig.secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(jwtConfig.issuer).build()

    fun getAuthorizationHeaderValue(userPrincipal: UserPrincipal): String = "Bearer ${generateToken(userPrincipal)}"

    fun validateToken(jwtCredential: JWTCredential): UserPrincipal {
        val userPrincipal = jacksonObjectMapper().readValue(
            jwtCredential.payload.getClaim("user").asString(),
            UserPrincipal::class.java
        )
        val foundUser = runBlocking { UserRepository().findOne(userPrincipal.id) }

        return foundUser?.let { userPrincipal } ?: throw AuthenticationException("Invalid token")
    }

    private fun generateToken(userPrincipal: UserPrincipal): String {
        val userPrincipalString = jacksonObjectMapper().writeValueAsString(userPrincipal)
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(jwtConfig.issuer)
            .withClaim("user", userPrincipalString)
            .sign(algorithm)
    }
}
