package io.ducket.api.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ducket.api.config.AppConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.exception.FlywayValidateException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.sql.Connection.TRANSACTION_REPEATABLE_READ

class AppDatabaseFactory(appConfig: AppConfig) : DatabaseFactory {
    private val config = appConfig.databaseConfig

    override fun connect() {
        val hikari = getSource()

        Database.connect(
            datasource = hikari,
            databaseConfig = DatabaseConfig.invoke {
                useNestedTransactions = true
            }
        )

        val flyway = Flyway.configure()
            // .baselineOnMigrate(true)
            .dataSource(hikari)
            .locations("classpath:io/ducket/api/app/database/migration")
            .load()

        try {
            flyway.info()
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
        val databaseUrl = "jdbc:mysql://${config.host}:${config.port}/${config.database}" +
                "?useUnicode=true" +
                "&characterEncoding=utf8" +
                "&serverTimezone=UTC" +
                "&autoReconnect=true" +
                "&useSSL=false" +
                "&allowPublicKeyRetrieval=true"

        return HikariConfig().let { hikariConfig ->
            hikariConfig.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            hikariConfig.jdbcUrl = databaseUrl
            hikariConfig.driverClassName = config.driver
            hikariConfig.schema = config.database
            hikariConfig.username = config.user
            hikariConfig.password = config.password
            hikariConfig.maximumPoolSize = 3
            hikariConfig.isAutoCommit = false
            hikariConfig.validate()

            return@let HikariDataSource(hikariConfig)
        }
    }

    suspend fun <T> dbQuery(
        block: suspend () -> T
    ): T = newSuspendedTransaction(transactionIsolation = TRANSACTION_REPEATABLE_READ) { block() }
}