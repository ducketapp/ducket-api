package io.ducket.api.domain.model.currency

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import java.math.BigDecimal
import java.time.LocalDate

internal object CurrencyRatesTable : LongIdTable("currency_rate") {
    val baseCurrencyIsoCode = reference("base_currency_iso_code", CurrenciesTable.isoCode)
    val quoteCurrencyIsoCode = reference("quote_currency_iso_code", CurrenciesTable.isoCode)
    val rate = decimal("rate", 10, 4)
    val date = date("date")
    val dataSource = varchar("data_source", 64).nullable()

    init {
        uniqueIndex("base_quote_date_unique_index", baseCurrencyIsoCode, quoteCurrencyIsoCode, date)
    }
}

class CurrencyRateEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CurrencyRateEntity>(CurrencyRatesTable)

    var baseCurrency by CurrencyEntity referencedOn CurrencyRatesTable.baseCurrencyIsoCode
    var quoteCurrency by CurrencyEntity referencedOn CurrencyRatesTable.quoteCurrencyIsoCode

    var rate by CurrencyRatesTable.rate
    var date by CurrencyRatesTable.date
    var dataSource by CurrencyRatesTable.dataSource

    fun toModel() = CurrencyRate(
        id.value,
        baseCurrency.toModel(),
        quoteCurrency.toModel(),
        rate,
        date,
        dataSource,
    )
}

data class CurrencyRate(
    val id: Long,
    val baseCurrency: Currency,
    val quoteCurrency: Currency,
    val rate: BigDecimal,
    val date: LocalDate,
    val dataSource: String?,
)

data class CurrencyRateCreate(
    val baseCurrency: String,
    val quoteCurrency: String,
    val rate: BigDecimal,
    val date: LocalDate,
    val dataSource: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrencyRateCreate

        if (baseCurrency != other.baseCurrency) return false
        if (quoteCurrency != other.quoteCurrency) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = baseCurrency.hashCode()
        result = 31 * result + quoteCurrency.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}
