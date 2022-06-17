package io.ducket.api.config

data class DatabaseServerConfig(
    val schema: DatabaseServerSchemaConfig,
    val driver: String,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
)