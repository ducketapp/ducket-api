package io.ducket.api.domain.repository

import domain.model.user.UserEntity
import io.ducket.api.domain.controller.label.LabelCreateDto
import io.ducket.api.domain.model.label.Label
import io.ducket.api.domain.model.label.LabelEntity
import io.ducket.api.domain.model.label.LabelsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class LabelRepository {

    fun create(userId: String, labelDto: LabelCreateDto): Label = transaction {
        LabelEntity.new {
            name = labelDto.name
            user = UserEntity[userId]
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findOneByNameAndUserId(userId: String, name: String): Label? = transaction {
        LabelEntity.find {
            LabelsTable.name.eq(name).and(LabelsTable.userId.eq(userId))
        }.firstOrNull()?.toModel()
    }

    fun findAllByUserId(userId: String): List<Label> = transaction {
        LabelEntity.find { LabelsTable.userId.eq(userId) }.map { it.toModel() }
    }
}