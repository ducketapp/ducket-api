package io.ducket.api.config

class AppConfig {
    lateinit var serverConfig: ServerConfig
    lateinit var databaseServerConfig: DatabaseServerConfig
    lateinit var jwtConfig: JwtConfig
    lateinit var localDataConfig: LocalDataConfig
}