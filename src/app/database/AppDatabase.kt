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
    fun close()
    fun getHikariConfig(): HikariConfig
    fun getDatabaseName(): String

    /**
     * Prevents both dirty and non-repeatable reads, but still allows for phantom reads.
     * A phantom read is when a transaction ("Transaction A") selects a list of rows through a WHERE clause,
     * another transaction ("Transaction B") performs an INSERT or DELETE with a row that satisfies Transaction A's WHERE clause,
     * and Transaction A selects using the same WHERE clause again, resulting in an inconsistency.
     */
    suspend fun <T> doTransaction(block: suspend () -> T): T {
        return newSuspendedTransaction(
            db = database,
            context = Dispatchers.IO,
            transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ
        ) {
            block()
        }
    }
}