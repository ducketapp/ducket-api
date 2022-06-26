package io.ducket.api.app.database.migrations

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("unused", "ClassName")
class V2__Set_timezone: BaseJavaMigration() {

    override fun migrate(context: Context?) {
        // creates timezone offset in +00:00 format
        val timezoneOffset = DateTimeFormatter.ofPattern("xxx").format(
            TimeZone.getDefault().toZoneId().rules.getOffset(Instant.now())
        )

        val setGlobalTimezoneStatement = "SET @@global.time_zone = `$timezoneOffset`;"
        val setSessionTimezoneStatement = "SET @@session.time_zone = `$timezoneOffset`;"

        transaction {
            TransactionManager.current().connection.prepareStatement(setGlobalTimezoneStatement, false).executeUpdate()
            TransactionManager.current().connection.prepareStatement(setSessionTimezoneStatement, false).executeUpdate()
        }
    }
}