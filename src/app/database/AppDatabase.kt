package dev.ducketapp.service.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

interface AppDatabase {
    var database: Database
    val dataSource: HikariDataSource

    fun connect()
    fun getHikariConfig(): HikariConfig
    fun getDatabaseName(): String
}
