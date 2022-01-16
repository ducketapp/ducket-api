package io.ducket.api.config

data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String,
    val driver: String,
    val dataPath: String,
)