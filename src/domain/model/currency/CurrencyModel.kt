package domain.model.currency

import io.ducket.api.domain.model.StringIdTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column


internal object CurrenciesTable : StringIdTable("currency") {
    val area = varchar("area", 45)
    val name = varchar("name", 45)
    val symbol = varchar("symbol", 45)
    val isoCode = varchar("iso_code", 3)
}

class CurrencyEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, CurrencyEntity>(CurrenciesTable)

    var area by CurrenciesTable.area
    var name by CurrenciesTable.name
    var symbol by CurrenciesTable.symbol
    var isoCode by CurrenciesTable.isoCode

    fun toModel() = Currency(id.value, area, name, symbol, isoCode)
}

data class Currency(
    val id: String,
    val area: String,
    val name: String,
    val symbol: String,
    val isoCode: String,
)