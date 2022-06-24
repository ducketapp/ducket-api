package io.ducket.api.app.database

import com.zaxxer.hikari.HikariConfig
import io.ducket.api.config.AppConfig
import org.jetbrains.exposed.sql.transactions.TransactionManager

class TestMainDatabase(appConfig: AppConfig): MainDatabase(appConfig) {

    override fun getHikariConfig(): HikariConfig {
        val databaseUrl = "jdbc:h2:mem:${getDatabaseName()};DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;MODE=MySQL"

        return HikariConfig().apply {
            jdbcUrl = databaseUrl
            driverClassName = databaseConfig.driver
            username = databaseConfig.user
            password = databaseConfig.password
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
    }

    fun close() {
        dataSource.close()
        TransactionManager.closeAndUnregister(database)
    }

    override fun getDatabaseName(): String {
        return databaseConfig.schema.main
    }
}