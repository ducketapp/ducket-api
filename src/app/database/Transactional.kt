package io.ducket.api.app.database

import io.ducket.api.app.di.AppModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.sql.Connection

interface Transactional: KoinComponent {
    private val mainDatabase: Lazy<AppDatabase> get() = inject(named(AppModule.DatabaseType.MAIN_DB))

    suspend fun <T> blockingTransaction(statement: suspend Transaction.() -> T): T {
//        return newSuspendedTransaction(
//            context = Dispatchers.IO,
//            db = database.value.database,
//            transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ
//        ) {
//            statement()
//        }
        return transaction(
            db = mainDatabase.value.database,
            transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ,
            repetitionAttempts = 3,
        ) {
            // Has to be blocking since JDBC driver acquires connection synchronously
            // and calling suspended transactions can lead to unexpected behavior
            // due to the inability to share transactions when the context changes.
            runBlocking { statement() }
        }
//        return withContext(Dispatchers.IO) {
//            transaction(db = database.value.database) { runBlocking { statement() } }
//        }
    }
}