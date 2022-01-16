package io.ducket.api.app.database

import com.zaxxer.hikari.HikariDataSource

interface DatabaseFactory {
    fun connect()
    fun close()
    fun getSource(): HikariDataSource
}