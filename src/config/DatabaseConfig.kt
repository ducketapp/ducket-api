package io.budgery.api.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun setup(jdbcUrl: String, username: String, password: String) {
        val config = HikariConfig().also { config ->
            config.jdbcUrl = jdbcUrl
            config.username = username
            config.password = password
            config.maximumPoolSize = 3
            config.isAutoCommit = false
        }
        val db = Database.connect(HikariDataSource(config))
        db.useNestedTransactions = true
    }
}