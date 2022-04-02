package domain.model.imports

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object ImportsTable : LongIdTable("import") {
    val userId = reference("user_id", UsersTable)
    val filePath = varchar("file_path", 64)
    val importedAt = timestamp("imported_at")
}

class ImportEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ImportEntity>(ImportsTable)

    var user by UserEntity referencedOn ImportsTable.userId
    var filePath by ImportsTable.filePath
    var importedAt by ImportsTable.importedAt

    fun toModel() = Import(
        id.value,
        user.toModel(),
        filePath,
        importedAt,
    )
}

data class Import(
    val id: Long,
    val user: User,
    val filePath: String,
    val importedAt: Instant,
)
