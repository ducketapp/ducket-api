package io.ducket.api.app.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

interface AppDatabase {
    var database: Database
    val dataSource: HikariDataSource

    fun connect()
    fun getHikariConfig(): HikariConfig
    fun getDatabaseName(): String

    @Deprecated("Use Transactional interface instead")
    suspend fun <T> newTransaction(block: suspend () -> T): T {
        return newSuspendedTransaction(
            db = database,
            context = Dispatchers.IO,
            transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ
        ) {
            block()
        }
    }
}
