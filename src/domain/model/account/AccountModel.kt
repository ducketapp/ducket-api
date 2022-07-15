package io.ducket.api.domain.model.account

import io.ducket.api.domain.model.currency.CurrenciesTable
import io.ducket.api.domain.model.currency.Currency
import io.ducket.api.domain.model.currency.CurrencyEntity
import io.ducket.api.domain.model.operation.OperationEntity
import io.ducket.api.domain.model.operation.OperationsTable
import io.ducket.api.domain.model.user.User
import io.ducket.api.domain.model.user.UserEntity
import io.ducket.api.domain.model.user.UsersTable
import io.ducket.api.app.AccountType
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.app.OperationType
import io.ducket.api.utils.sumByDecimal
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object AccountsTable : LongIdTable("account") {
    val extId = varchar("ext_id", 128).nullable()
    val userId = reference("user_id", UsersTable)
    val currencyId = reference("currency_id", CurrenciesTable)
    val type = enumerationByName("type", 32, AccountType::class)
    val title = varchar("title", 64)
    val startBalance = decimal("start_balance", 10, DEFAULT_SCALE).clientDefault { BigDecimal.ZERO }
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }

    init {
        uniqueIndex("unique_index", userId, title, extId)
    }
}

class AccountEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AccountEntity>(AccountsTable)

    var extId by AccountsTable.extId
    var title by AccountsTable.title
    var startBalance by AccountsTable.startBalance
    var notes by AccountsTable.notes
    var user by UserEntity referencedOn AccountsTable.userId
    var currency by CurrencyEntity referencedOn AccountsTable.currencyId
    var type by AccountsTable.type
    var createdAt by AccountsTable.createdAt
    var modifiedAt by AccountsTable.modifiedAt

    private val operations by OperationEntity referrersOn OperationsTable.accountId
    private val incomingTransfers by OperationEntity optionalReferrersOn OperationsTable.transferAccountId

    private val totalBalance: BigDecimal
        get() = startBalance.plus()
            .plus(operations.sumByDecimal { if (it.type == OperationType.INCOME) it.postedAmount else it.postedAmount.negate() })
            .plus(incomingTransfers.sumByDecimal { it.clearedAmount })

    fun toModel() = Account(
        id.value,
        extId,
        title,
        startBalance,
        totalBalance,
        notes,
        user.toModel(),
        currency.toModel(),
        type,
        createdAt,
        modifiedAt,
    )
}

data class Account(
    val id: Long,
    val extId: String?,
    val title: String,
    val startBalance: BigDecimal,
    val totalBalance: BigDecimal,
    val notes: String?,
    val user: User,
    val currency: Currency,
    val type: AccountType,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

data class AccountCreate(
    val extId: String?,
    val title: String,
    val startBalance: BigDecimal,
    val notes: String?,
    val userId: Long,
    val currency: String,
    val type: AccountType,
)

data class AccountUpdate(
    val title: String,
    val startBalance: BigDecimal,
    val notes: String?,
    val type: AccountType,
)
