package io.ducket.api.config

data class ServerConfig(
    val env: String,
    val host: String,
    val port: Int,
)
