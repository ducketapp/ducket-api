package org.expenny.service.app.database

import com.zaxxer.hikari.HikariConfig
import org.expenny.service.config.AppConfig
import org.jetbrains.exposed.sql.transactions.TransactionManager

class TestMainDatabase(appConfig: AppConfig): MainDatabase(appConfig) {

    override fun getHikariConfig(): HikariConfig {
        val databaseUrl = "jdbc:h2:mem:test;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;MODE=MySQL"

        return HikariConfig().apply {
            jdbcUrl = databaseUrl
            driverClassName = "org.h2.Driver"
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
        return "test"
    }
}