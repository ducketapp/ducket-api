package io.ducket.api.domain.model.transfer

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.CombinedIdTable
import io.ducket.api.domain.model.StringIdTable
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*

internal object TransfersTable : StringIdTable("transfer") {
    val userId = reference("user_id", UsersTable)
    val accountId = reference("account_id", AccountsTable)
    val transferAccountId = reference("transfer_account_id", AccountsTable)
    val relationId = varchar("transfer_relation_id", 36)
    val exchangeRate = decimal("exchange_rate", 10, 4)
    val amount = decimal("amount", 10, 2)
    val date = timestamp("date")
    val notes = varchar("notes", 128).nullable()
    val longitude = varchar("longitude", 45).nullable()
    val latitude = varchar("latitude", 45).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class TransferEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, TransferEntity>(TransfersTable)

    var transferAccount by AccountEntity referencedOn TransfersTable.transferAccountId
    var account by AccountEntity referencedOn TransfersTable.accountId
    var user by UserEntity referencedOn TransfersTable.userId
    var relationId by TransfersTable.relationId
    var exchangeRate by TransfersTable.exchangeRate
    var amount by TransfersTable.amount
    var date by TransfersTable.date
    var notes by TransfersTable.notes
    var longitude by TransfersTable.longitude
    var latitude by TransfersTable.latitude
    var createdAt by TransfersTable.createdAt
    var modifiedAt by TransfersTable.modifiedAt

    var attachments by AttachmentEntity via TransferAttachmentsTable

    fun toModel() = Transfer(
        id.value,
        transferAccount.toModel(),
        account.toModel(),
        user.toModel(),
        relationId,
        exchangeRate,
        amount,
        date,
        notes,
        longitude,
        latitude,
        attachments.toList().map { it.toModel() },
        createdAt,
        modifiedAt,
    )
}

class Transfer(
    val id: String,
    val transferAccount: Account,
    val account: Account,
    val user: User,
    val relationId: String,
    val exchangeRate: BigDecimal,
    val amount: BigDecimal,
    val date: Instant,
    val notes: String?,
    val longitude: String?,
    val latitude: String?,
    val attachments: List<Attachment>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)