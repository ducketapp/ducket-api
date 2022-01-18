package io.ducket.api.config

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val name: String,
    val username: String,
    val password: String,
    val driver: String,
    val dataPath: String,
) {
    val url = "jdbc:mysql://$host:$port/$name?useUnicode=true&serverTimezone=UTC&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true"
}