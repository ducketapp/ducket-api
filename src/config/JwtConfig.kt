package io.budgery.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.budgery.api.AuthenticationException
import io.ktor.application.*
import io.ktor.auth.*
import java.util.*

object JwtConfig {
    private const val secret = "cn#PGH7EgbO87VBa!"
    private const val issuer = "io.budgery.api"
    private const val validityInMs = 36_000_00 * 24 // 1 day
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun getPrincipal(authContext: AuthenticationContext): UserPrincipal {
        return authContext.principal() ?: throw AuthenticationException("Invalid auth token content")
    }

    fun generate(userPrincipal: UserPrincipal): String = JWT.create()
        // .withExpiresAt(getExpiration())
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", userPrincipal.id)
        .withClaim("uuid", userPrincipal.uuid.toString())
        .withClaim("email", userPrincipal.email)
        .sign(algorithm)

    // private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}
