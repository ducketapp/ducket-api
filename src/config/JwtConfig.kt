package dev.ducketapp.service.config

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
)