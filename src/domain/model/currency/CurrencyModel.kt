package domain.model.currency

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import java.math.BigDecimal

const val DEFAULT_SCALE = 4
const val DEFAULT_ROUNDING = BigDecimal.ROUND_HALF_EVEN

internal object CurrenciesTable : LongIdTable("currency") {
    val area = varchar("area", 32)
    val name = varchar("name", 32)
    val symbol = varchar("symbol", 8)
    val isoCode = varchar("iso_code", 3).uniqueIndex()
}

class CurrencyEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CurrencyEntity>(CurrenciesTable)

    var area by CurrenciesTable.area
    var name by CurrenciesTable.name
    var symbol by CurrenciesTable.symbol
    var isoCode by CurrenciesTable.isoCode

    fun toModel() = Currency(
        id.value,
        area,
        name,
        symbol,
        isoCode,
    )
}

data class Currency(
    val id: Long,
    val area: String,
    val name: String,
    val symbol: String,
    val isoCode: String,
)
