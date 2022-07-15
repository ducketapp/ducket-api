package io.ducket.api.domain.model.debt

import io.ducket.api.domain.model.account.Account
import io.ducket.api.domain.model.account.AccountEntity
import io.ducket.api.domain.model.account.AccountsTable
import io.ducket.api.domain.model.operation.Operation
import io.ducket.api.domain.model.operation.OperationEntity
import io.ducket.api.domain.model.user.User
import io.ducket.api.domain.model.user.UserEntity
import io.ducket.api.domain.model.user.UsersTable
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.app.DebtType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

internal object DebtsTable : LongIdTable("debt") {
    val userId = reference("user_id", UsersTable)
    val accountId = reference("account_id", AccountsTable)
    val title = varchar("title", 32)
    val description = varchar("description", 128).nullable()
    val type = enumerationByName("type", 32, DebtType::class)
    val amount = decimal("amount", 10, DEFAULT_SCALE)
    val date = date("date")
    val dueDate = date("due_date").nullable()
    val closeDate = date("close_date").nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class DebtEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<DebtEntity>(DebtsTable)

    var user by UserEntity referencedOn DebtsTable.userId
    var account by AccountEntity referencedOn DebtsTable.accountId

    var title by DebtsTable.title
    var description by DebtsTable.description
    var type by DebtsTable.type
    var amount by DebtsTable.amount
    var date by DebtsTable.date
    var dueDate by DebtsTable.dueDate
    var closeDate by DebtsTable.closeDate
    var createdAt by DebtsTable.createdAt
    var modifiedAt by DebtsTable.modifiedAt

    val operations by OperationEntity via DebtOperationsTable

    fun toModel() = Debt(
        id.value,
        user.toModel(),
        account.toModel(),
        title,
        description,
        type,
        amount,
        date,
        dueDate,
        closeDate,
        operations.map { it.toModel() },
        createdAt,
        modifiedAt,
    )
}

data class Debt(
    val id: Long,
    val user: User,
    val account: Account,
    val title: String,
    val description: String?,
    val type: DebtType,
    val amount: BigDecimal,
    val date: LocalDate,
    val dueDate: LocalDate?,
    val closeDate: LocalDate?,
    val operations: List<Operation>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)