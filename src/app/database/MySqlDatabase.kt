package dev.ducketapp.service.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.ducketapp.service.config.AppConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig

abstract class MySqlDatabase(appConfig: AppConfig): AppDatabase {
    val databaseConfig = appConfig.databaseServerConfig

    override lateinit var database: Database

    override val dataSource: HikariDataSource by lazy {
        val config = getHikariConfig()
        HikariDataSource(config)
    }

    override fun connect() {
        database = Database.connect(
            datasource = dataSource,
            databaseConfig = DatabaseConfig.invoke { useNestedTransactions = false }
        )
    }

    override fun getHikariConfig(): HikariConfig {
        val url = "jdbc:mysql://${databaseConfig.host}:${databaseConfig.port}/${getDatabaseName()}" +
                "?useUnicode=true" +
                "&characterEncoding=utf8" +
                "&serverTimezone=UTC" +
                "&autoReconnect=true" +
                "&useSSL=false" +
                "&allowPublicKeyRetrieval=true"

        return HikariConfig().apply {
            jdbcUrl = url
            driverClassName = databaseConfig.driver
            poolName = "${getDatabaseName()}-pool"
            username = databaseConfig.user
            password = databaseConfig.password
            maximumPoolSize = 25
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            addDataSourceProperty("useServerPrepStmts", "true")
            addDataSourceProperty("useLocalSessionState", "true")
            addDataSourceProperty("rewriteBatchedStatements", "true")
            addDataSourceProperty("cacheResultSetMetadata", "true")
            addDataSourceProperty("cacheServerConfiguration", "true")
            addDataSourceProperty("elideSetAutoCommits", "true")
            addDataSourceProperty("maintainTimeStats", "false")
        }
    }
}