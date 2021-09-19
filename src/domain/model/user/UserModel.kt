package domain.model.user

import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import io.budgery.api.domain.model.attachment.Attachment
import io.budgery.api.domain.model.attachment.AttachmentEntity
import io.budgery.api.domain.model.attachment.AttachmentsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

internal object UsersTable : IntIdTable("user") {
    val uuid = varchar("uuid", 36).default(UUID.randomUUID().toString())
    val name = varchar("name", 45)
    val email = varchar("email", 45).uniqueIndex()
    val mainCurrencyId = reference("main_currency_id", CurrenciesTable)
    val attachmentId = optReference("attachment_id", AttachmentsTable)
    val passwordHash = varchar("password_hash", 64)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class UserEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UsersTable)

    var uuid by UsersTable.uuid
    var name by UsersTable.name
    var email by UsersTable.email
    var mainCurrency by CurrencyEntity referencedOn UsersTable.mainCurrencyId
    var attachment by AttachmentEntity optionalReferencedOn UsersTable.attachmentId
    var passwordHash by UsersTable.passwordHash
    var createdAt by UsersTable.createdAt
    var modifiedAt by UsersTable.modifiedAt

    fun toModel() = User(
        id.value,
        UUID.fromString(uuid),
        name,
        email,
        mainCurrency.toModel(),
        attachment?.toModel(),
        passwordHash,
        createdAt,
        modifiedAt
    )
}

data class User(
    val id: Int,
    val uuid: UUID,
    val name: String,
    val email: String,
    val mainCurrency: Currency,
    val attachment: Attachment?,
    val passwordHash: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
)