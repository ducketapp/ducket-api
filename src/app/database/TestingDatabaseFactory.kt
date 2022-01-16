package io.ducket.api.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class TestingDatabaseFactory: DatabaseFactory {
    lateinit var hikariDataSource: HikariDataSource

    override fun connect() {
        val hikari = getSource()
        Database.connect(hikari).apply { useNestedTransactions = true }

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
            hikariConfig.jdbcUrl = "jdbc:h2:mem:ducket;MODE=MySQL;INIT=RUNSCRIPT FROM 'classpath:db/migration/V1_0__db-schema.sql';DB_CLOSE_DELAY=-1"
            hikariConfig.username = "root"
            hikariConfig.password = "toor"
            hikariConfig.maximumPoolSize = 3
            hikariConfig.isAutoCommit = false
            hikariConfig.validate()

            return@let HikariDataSource(hikariConfig)
        }
    }
}