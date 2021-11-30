package io.ducket.api.domain.model.label

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.StringIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

internal object LabelsTable : StringIdTable("record_label") {
    val name = varchar("name", 45)
    val userId = reference("user_id", UsersTable)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class LabelEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, LabelEntity>(LabelsTable)

    var name by LabelsTable.name
    var user by UserEntity referencedOn LabelsTable.userId
    var createdAt by LabelsTable.createdAt
    var modifiedAt by LabelsTable.modifiedAt

    fun toModel() = Label(id.value, name, user.toModel(), createdAt, modifiedAt)
}

data class Label(
    val id: String,
    val name: String,
    val user: User,
    val createdAt: Instant,
    val modifiedAt: Instant,
)
