package io.ducket.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ducket.api.auth.UserPrincipal
import io.ducket.api.domain.repository.UserRepository
import io.ducket.api.plugins.AuthenticationException
import io.ktor.auth.jwt.*

class JwtManager(appConfig: AppConfig) {
    // private const val secret = "cn#PGH7EgbO87VBa!"
    private val jwtConfig = appConfig.jwtConfig
    private val algorithm = Algorithm.HMAC256(jwtConfig.secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(jwtConfig.issuer).build()

    fun generateToken(userPrincipal: UserPrincipal): String {
        val userPrincipalString = jacksonObjectMapper().writeValueAsString(userPrincipal)
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(jwtConfig.issuer)
            .withClaim("user", userPrincipalString)
            .sign(algorithm)
    }

    fun validateToken(jwtCredential: JWTCredential): UserPrincipal {
        val userPrincipal = jacksonObjectMapper().readValue(
            jwtCredential.payload.getClaim("user").asString(),
            UserPrincipal::class.java
        )

        return UserRepository().findOne(userPrincipal.id)?.let { userPrincipal }
            ?: throw AuthenticationException("Invalid token: user not found")
    }
}
