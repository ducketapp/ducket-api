package org.expenny.service.config

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
)