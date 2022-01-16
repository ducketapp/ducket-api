package io.ducket.api.domain.model.transfer

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import domain.model.imports.Import
import domain.model.imports.ImportEntity
import domain.model.imports.ImportsTable
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object TransfersTable : LongIdTable("transfer") {
    val userId = reference("user_id", UsersTable)
    val accountId = reference("account_id", AccountsTable)
    val transferAccountId = reference("transfer_account_id", AccountsTable)
    val importId = reference("import_id", ImportsTable).nullable()
    val relationCode = varchar("relation_code", 36).nullable()
    val exchangeRate = decimal("exchange_rate", 10, 4)
    val amount = decimal("amount", 10, 2)
    val date = timestamp("date")
    val notes = varchar("notes", 128).nullable()
    val longitude = varchar("longitude", 45).nullable()
    val latitude = varchar("latitude", 45).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class TransferEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TransferEntity>(TransfersTable)

    var transferAccount by AccountEntity referencedOn TransfersTable.transferAccountId
    var account by AccountEntity referencedOn TransfersTable.accountId
    var user by UserEntity referencedOn TransfersTable.userId
    var import by ImportEntity optionalReferencedOn TransfersTable.importId
    var relationCode by TransfersTable.relationCode
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
        import?.toModel(),
        relationCode,
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
    val id: Long,
    val transferAccount: Account,
    val account: Account,
    val user: User,
    val import: Import?,
    val relationCode: String?,
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
