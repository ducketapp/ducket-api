package io.ducket.api.domain.controller.record

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import domain.model.transaction.Transaction
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.category.TypedCategoryDto
import io.ducket.api.domain.model.transfer.Transfer
import java.math.BigDecimal
import java.time.Instant

open class RecordDto {
    val id: String
    val amount: BigDecimal
    val isExpense: Boolean
    val isTransfer: Boolean
    val account: AccountDto
    val category: TypedCategoryDto?
    val notes: String?
    val longitude: String?
    val latitude: String?
    val attachments: List<AttachmentDto>?
    @JsonSerialize(using = InstantSerializer::class) val date: Instant
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant

    constructor(transfer: Transfer) {
        this.id = transfer.id
        this.isTransfer = true
        this.isExpense = transfer.amount < BigDecimal.ZERO
        this.amount = transfer.amount
        this.account = AccountDto(transfer.account)
        this.category = null
        this.date = transfer.date
        this.notes = transfer.notes
        this.longitude = transfer.longitude
        this.latitude = transfer.latitude
        this.attachments = transfer.attachments.map { AttachmentDto(it) }
        this.createdAt = transfer.createdAt
        this.modifiedAt = transfer.modifiedAt
    }

    constructor(transaction: Transaction) {
        this.id = transaction.id
        this.isTransfer = false
        this.isExpense = transaction.amount < BigDecimal.ZERO
        this.amount = transaction.amount
        this.account = AccountDto(transaction.account)
        this.category = transaction.category?.let { TypedCategoryDto(it) }
        this.date = transaction.date
        this.notes = transaction.notes
        this.longitude = transaction.longitude
        this.latitude = transaction.latitude
        this.attachments = transaction.attachments.map { AttachmentDto(it) }
        this.createdAt = transaction.createdAt
        this.modifiedAt = transaction.modifiedAt
    }
}