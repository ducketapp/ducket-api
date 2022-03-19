package io.ducket.api.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ducket.api.config.AppConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.exception.FlywayValidateException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig

class AppDatabaseFactory(appConfig: AppConfig): DatabaseFactory {
    private val config = appConfig.databaseConfig

    override fun connect() {
        val hikari = getSource()

        Database.connect(
            datasource = hikari,
            databaseConfig = DatabaseConfig.invoke {
                useNestedTransactions = true
            }
        )

        val flyway = Flyway.configure().baselineOnMigrate(true).dataSource(hikari).load()
        flyway.info()

        try {
            flyway.migrate()
        } catch (e: FlywayValidateException) {
            flyway.repair()
            flyway.migrate()
        }
    }

    override fun close() {
        // ignore
    }

    override fun getSource(): HikariDataSource {
        return HikariConfig().let { hikariConfig ->
            hikariConfig.driverClassName = config.driver
            hikariConfig.jdbcUrl = config.url
            hikariConfig.username = config.user
            hikariConfig.password = config.password
            hikariConfig.maximumPoolSize = 3
            hikariConfig.isAutoCommit = false
            hikariConfig.validate()

            return@let HikariDataSource(hikariConfig)
        }
    }
}