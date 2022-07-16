package dev.ducket.api.app.database

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.Connection

interface Transactional: KoinComponent {
    private val mainDatabase: Lazy<AppDatabase> get() = inject()

    suspend fun <T> blockingTransaction(statement: suspend Transaction.() -> T): T {
        return transaction(
            db = mainDatabase.value.database,
            transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ,
            repetitionAttempts = 3,
        ) {
            // It has to be blocking since JDBC driver acquires connection synchronously
            // and calling suspended transactions can lead to unexpected behavior
            // due to the inability to share transactions when the context changes.
            runBlocking { statement() }
        }
    }
}