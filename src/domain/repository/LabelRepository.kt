package io.budgery.api.domain.repository

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import domain.model.user.UserEntity
import io.budgery.api.domain.controller.label.LabelCreateDto
import io.budgery.api.domain.model.label.Label
import io.budgery.api.domain.model.label.LabelEntity
import io.budgery.api.domain.model.label.LabelsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime

class LabelRepository {

    fun create(userId: Int, labelDto: LabelCreateDto): Label = transaction {
        LabelEntity.new {
            name = labelDto.name
            user = UserEntity[userId]
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findOneByNameAndUserId(name: String, userId: Int) : Label? = transaction {
        LabelEntity.find { LabelsTable.name.eq(name).and(LabelsTable.userId.eq(userId)) }.firstOrNull()?.toModel()
    }

    fun findAllByUserId(userId: Int): List<Label> = transaction {
        LabelEntity.find { LabelsTable.userId.eq(userId) }.map { it.toModel() }
    }
}