package io.budgery.api.domain.repository

import domain.model.imports.Import
import domain.model.imports.ImportEntity
import domain.model.imports.ImportsTable
import org.jetbrains.exposed.sql.transactions.transaction


class ImportRepository {

    fun getAllByUserId(userId: Int): List<Import> = transaction {
        ImportEntity.find { ImportsTable.userId.eq(userId) }.map { it.toModel() }
    }

    fun getOneByUserId(userId: Int): Import? = transaction {
        ImportEntity.find { ImportsTable.userId.eq(userId) }.firstOrNull()?.toModel()
    }
}