package org.expenny.service.domain.model.account

import org.expenny.service.domain.model.currency.CurrenciesTable
import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.model.currency.CurrencyEntity
import org.expenny.service.domain.model.operation.OperationEntity
import org.expenny.service.domain.model.operation.OperationsTable
import org.expenny.service.domain.model.user.User
import org.expenny.service.domain.model.user.UserEntity
import org.expenny.service.domain.model.user.UsersTable
import org.expenny.service.app.AccountType
import org.expenny.service.app.DEFAULT_SCALE
import org.expenny.service.app.OperationType
import org.expenny.service.utils.sumByDecimal
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
    val name = varchar("name", 64)
    val startBalance = decimal("start_balance", 10, DEFAULT_SCALE).clientDefault { BigDecimal.ZERO }
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }

    init {
        uniqueIndex("account_unique_index", userId, name, extId)
    }
}

class AccountEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AccountEntity>(AccountsTable)

    var extId by AccountsTable.extId
    var name by AccountsTable.name
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
        name,
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
    val name: String,
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
    val name: String,
    val startBalance: BigDecimal,
    val notes: String?,
    val userId: Long,
    val currency: String,
    val type: AccountType,
)

data class AccountUpdate(
    val name: String,
    val startBalance: BigDecimal,
    val notes: String?,
    val type: AccountType,
)
