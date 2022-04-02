package io.ducket.api.config

data class DatabaseConfig(
    val driver: String,
    val database: String,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
)