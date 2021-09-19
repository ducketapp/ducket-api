package domain.model.imports

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

internal object ImportsTable : IntIdTable("file_import") {
    val userId = reference("user_id", UsersTable)
    val filePath = varchar("file_path", 45)
    val isExternal = bool("is_external")
    val importedAt = timestamp("imported_at")
}

class ImportEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ImportEntity>(ImportsTable)

    var user by UserEntity referencedOn ImportsTable.userId
    var filePath by ImportsTable.filePath
    var isExternal by ImportsTable.isExternal
    var importedAt by ImportsTable.importedAt

    fun toModel() = Import(id.value, user.toModel(), filePath, isExternal, importedAt)
}

data class Import(
    val id: Int,
    val user: User,
    val filePath: String,
    val isExternal: Boolean,
    val importedAt: Instant,
)