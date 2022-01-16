package io.ducket.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ducket.api.domain.repository.UserRepository
import io.ducket.api.plugins.AuthenticationException
import io.ktor.auth.*
import io.ktor.auth.jwt.*

class JwtManager(appConfig: AppConfig) {
    // private const val secret = "cn#PGH7EgbO87VBa!"
    private val jwtConfig = appConfig.jwtConfig
    private val algorithm = Algorithm.HMAC256(jwtConfig.secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(jwtConfig.issuer).build()

    fun generateToken(userPrincipal: UserPrincipal): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(jwtConfig.issuer)
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
