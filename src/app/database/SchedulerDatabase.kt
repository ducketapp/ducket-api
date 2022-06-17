package io.ducket.api.app.database

import io.ducket.api.config.AppConfig
import io.ducket.api.getLogger
import org.flywaydb.core.Flyway

class SchedulerDatabase(appConfig: AppConfig): MySqlDatabase(appConfig) {

    override fun connect() {
        super.connect()

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("database/migrations/scheduler")
            .load()

        try {
            flyway.info()
            flyway.migrate()
        } catch (e: Exception) {
            getLogger().error("Cannot perform the migration: ${e.message}")
            throw e
        }
    }

    override fun getDatabaseName(): String {
        return databaseConfig.schema.scheduler
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}