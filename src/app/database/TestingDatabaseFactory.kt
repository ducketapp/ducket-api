package io.ducket.api.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.exception.FlywayValidateException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class TestingDatabaseFactory: DatabaseFactory {
    lateinit var hikariDataSource: HikariDataSource

    override fun connect() {
        hikariDataSource = getSource()

        Database.connect(
            datasource = hikariDataSource,
            databaseConfig = DatabaseConfig.invoke {
                useNestedTransactions = true
            }
        )

        val flyway = Flyway.configure()
            // .baselineOnMigrate(true)
            .dataSource(hikariDataSource)
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
        hikariDataSource.close()
    }

    override fun getSource(): HikariDataSource {
        val databaseUrl = "jdbc:h2:mem:test;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;MODE=MySQL"

        return HikariConfig().let { hikariConfig ->
            hikariConfig.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            hikariConfig.jdbcUrl = databaseUrl
            hikariConfig.driverClassName = "org.h2.Driver"
            hikariConfig.username = "test"
            hikariConfig.password = "test"
            hikariConfig.maximumPoolSize = 3
            hikariConfig.isAutoCommit = false
            hikariConfig.validate()

            return@let HikariDataSource(hikariConfig)
        }
    }
}