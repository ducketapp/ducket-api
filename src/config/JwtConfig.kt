package io.ducket.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ducket.api.domain.repository.UserRepository
import io.ducket.api.plugins.AuthenticationException
import io.ducket.api.plugins.AuthorizationException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

object JwtConfig {
    // private const val secret = "cn#PGH7EgbO87VBa!"
    const val realm = "io.ducket.api"
    private const val issuer = "io.ducket.api"
    private var secret = System.getenv()["JWT_TOKEN"] ?: throw Exception("JWT_TOKEN property not specified")
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun getPrincipal(authContext: AuthenticationContext): UserPrincipal {
        return authContext.principal() ?: throw AuthenticationException("Invalid auth token data")
    }

    fun generateToken(userPrincipal: UserPrincipal): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", userPrincipal.id)
            .withClaim("email", userPrincipal.email)
            .sign(algorithm)
    }

    fun validateToken(jwtCredential: JWTCredential): UserPrincipal {
        val jwtUserId = jwtCredential.payload.getClaim("id").asLong()
        val jwtUserEmail = jwtCredential.payload.getClaim("email").asString()

        return UserRepository().findOne(jwtUserId)?.let {
            UserPrincipal(jwtUserId, jwtUserEmail)
        } ?: throw AuthenticationException("Invalid auth token: user not found")
    }
}
