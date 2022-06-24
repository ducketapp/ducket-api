package io.ducket.api.app.database

import io.ducket.api.app.di.AppModule
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.sql.Connection

interface Transactional: KoinComponent {
    private val database: Lazy<AppDatabase> get() = inject(named(AppModule.DatabaseType.MAIN_DB))

    suspend fun <T> transactional(block: suspend () -> T): T {
        return newSuspendedTransaction(
            context = Dispatchers.IO,
            db = database.value.database,
            transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ
        ) {
            block()
        }
    }
}