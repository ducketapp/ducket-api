package dev.ducketapp.service.app.database

import dev.ducketapp.service.config.AppConfig
import dev.ducketapp.service.getLogger
import org.flywaydb.core.Flyway

open class MainDatabase(appConfig: AppConfig): MySqlDatabase(appConfig) {

    override fun connect() {
        super.connect()

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:dev/ducket/api/app/database/migrations")
            .load()

        try {
            flyway.info()
            flyway.repair()
            flyway.migrate()
        } catch (e: Exception) {
            getLogger().error("Cannot perform the migration: ${e.message}")
            throw e
        }
    }

    override fun getDatabaseName(): String {
        return databaseConfig.name
    }
}