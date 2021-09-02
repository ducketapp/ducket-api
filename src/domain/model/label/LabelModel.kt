package io.budgery.api.domain.model.label

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant
import java.time.LocalDateTime

internal object LabelsTable : IntIdTable("record_label") {
    val name = varchar("name", 45)
    val userId = reference("user_id", UsersTable)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class LabelEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<LabelEntity>(LabelsTable)

    var name by LabelsTable.name
    var user by UserEntity referencedOn LabelsTable.userId
    var createdAt by LabelsTable.createdAt
    var modifiedAt by LabelsTable.modifiedAt

    fun toModel() = Label(id.value, name, user.toModel(), createdAt, modifiedAt)
}

data class Label(
    val id: Int,
    val name: String,
    val user: User,
    val createdAt: Instant,
    val modifiedAt: Instant,
)