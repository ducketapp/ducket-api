package io.ducket.api.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class TestingDatabaseFactory: DatabaseFactory {
    lateinit var hikariDataSource: HikariDataSource

    override fun connect() {
        val hikari = getSource()
        Database.connect(
            datasource = hikari,
            databaseConfig = DatabaseConfig.invoke {
                useNestedTransactions = true
            }
        )
//        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
//
//        val flyway = Flyway.configure()
//            .baselineOnMigrate(true)
//            .dataSource(hikari)
//            .load()
//
//        flyway.info()
//        flyway.migrate()
    }

    override fun close() {
        hikariDataSource.close()
    }

    override fun getSource(): HikariDataSource {
        return HikariConfig().let { hikariConfig ->
            hikariConfig.driverClassName = "org.h2.Driver"
            hikariConfig.jdbcUrl = "jdbc:h2:mem:ducket-db;MODE=MySQL;DATABASE_TO_UPPER=false;IGNORECASE=TRUE;INIT=RUNSCRIPT FROM 'classpath:db/migration/V1.0__Initial.sql';DB_CLOSE_DELAY=-1"
            hikariConfig.username = "sa"
            hikariConfig.password = "sa"
            hikariConfig.maximumPoolSize = 3
            hikariConfig.isAutoCommit = false
            hikariConfig.validate()

            return@let HikariDataSource(hikariConfig)
        }
    }
}