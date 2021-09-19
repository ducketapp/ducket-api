package io.budgery.api.domain.controller.record

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import domain.model.transaction.Transaction
import io.budgery.api.InstantSerializer
import io.budgery.api.domain.controller.imports.ImportDto
import io.budgery.api.domain.controller.account.AccountDto
import io.budgery.api.domain.controller.category.TypedCategoryDto
import io.budgery.api.domain.controller.label.LabelDto
import io.budgery.api.domain.model.transfer.Transfer
import java.math.BigDecimal
import java.time.Instant

open class RecordDto {
    val id: Int
    val amount: BigDecimal
    val isExpense: Boolean
    val isTransfer: Boolean
    val account: AccountDto
    val import: ImportDto?
    val payee: String
    val category: TypedCategoryDto
    val note: String?
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
        this.category = TypedCategoryDto(transfer.category)
        this.account = AccountDto(transfer.account)
        this.import = transfer.import?.let { ImportDto(transfer.import) }
        this.date = transfer.date
        this.payee = transfer.payee
        this.note = transfer.note
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
        this.category = TypedCategoryDto(transaction.category)
        this.account = AccountDto(transaction.account)
        this.import = transaction.import?.let { ImportDto(transaction.import) }
        this.date = transaction.date
        this.payee = transaction.payee
        this.note = transaction.note
        this.longitude = transaction.longitude
        this.latitude = transaction.latitude
        this.attachments = transaction.attachments.map { AttachmentDto(it) }
        this.createdAt = transaction.createdAt
        this.modifiedAt = transaction.modifiedAt
    }
}