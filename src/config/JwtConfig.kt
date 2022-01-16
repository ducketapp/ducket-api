package io.ducket.api.config

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val realm: String,
    val audience: String,
)