package dev.ducket.api.config

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
)