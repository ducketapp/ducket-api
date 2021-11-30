package io.ducket.api.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
//    fun setup(jdbcUrl: String, username: String, password: String) {
//        val config = HikariConfig().also { config ->
//            config.jdbcUrl = jdbcUrl
//            config.username = username
//            config.password = password
//            config.maximumPoolSize = 3
//            config.isAutoCommit = false
//        }
//        val db = Database.connect(HikariDataSource(config))
//        db.useNestedTransactions = true
//    }

    fun init(appConfig: ApplicationConfig): Database {
        val host = appConfig.property("db.host").getString()
        val port = appConfig.property("db.port").getString()
        val name = appConfig.property("db.name").getString()
        val username = appConfig.property("db.user").getString()
        val password = appConfig.property("db.pass").getString()

        val url = "jdbc:mysql://$host:$port/$name" +
                "?useUnicode=true" +
                "&serverTimezone=UTC" +
                "&autoReconnect=true" +
                "&useSSL=false" +
                "&allowPublicKeyRetrieval=true"

        return HikariConfig().let { config ->
            config.jdbcUrl = url
            config.username = username
            config.password = password
            config.maximumPoolSize = 3
            config.isAutoCommit = false

            val dataSource = HikariDataSource(config)
            return@let Database.connect(dataSource)
        }.apply {
            this.useNestedTransactions = true
        }
    }
}