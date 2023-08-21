package org.expenny.service.config

data class DatabaseServerConfig(
    val name: String,
    val driver: String,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
)