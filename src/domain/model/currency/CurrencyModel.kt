package domain.model.currency

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


internal object CurrenciesTable : IntIdTable("currency") {
    val territory = varchar("territory", 45)
    val name = varchar("name", 45)
    val symbol = varchar("symbol", 45)
    val isoCode = varchar("iso_code", 3)
}

class CurrencyEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<CurrencyEntity>(CurrenciesTable)

    var territory by CurrenciesTable.territory
    var name by CurrenciesTable.name
    var symbol by CurrenciesTable.symbol
    var isoCode by CurrenciesTable.isoCode

    fun toModel() = Currency(id.value, territory, name, symbol, isoCode)
}

data class Currency(
    val id: Int,
    val territory: String,
    val name: String,
    val symbol: String,
    val isoCode: String,
)