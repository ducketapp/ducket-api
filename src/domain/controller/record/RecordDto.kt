package io.ducket.api.domain.controller.record

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import domain.model.transaction.Transaction
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.category.TypedCategoryDto
import io.ducket.api.domain.controller.imports.ImportDto
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.transfer.Transfer
import java.math.BigDecimal
import java.time.Instant

open class RecordDto {
    val id: Long
    val amount: BigDecimal
    val isExpense: Boolean
    val isTransfer: Boolean
    val account: AccountDto
    val category: TypedCategoryDto?
    val import: ImportDto?
    val owner: UserDto
    val notes: String?
    val longitude: String?
    val latitude: String?
    val images: List<RecordImageDto>?
    @JsonSerialize(using = InstantSerializer::class) val date: Instant

    constructor(transfer: Transfer) {
        this.id = transfer.id
        this.isTransfer = true
        this.isExpense = transfer.amount < BigDecimal.ZERO
        this.amount = transfer.amount
        this.account = AccountDto(transfer.account)
        this.category = null
        this.import = transfer.import?.let { ImportDto(it) }
        this.owner = UserDto(transfer.user)
        this.date = transfer.date
        this.notes = transfer.notes
        this.longitude = transfer.longitude
        this.latitude = transfer.latitude
        this.images = transfer.attachments.map { TransferImageDto(id, it) }
    }

    constructor(transaction: Transaction) {
        this.id = transaction.id
        this.isTransfer = false
        this.isExpense = transaction.amount < BigDecimal.ZERO
        this.amount = transaction.amount
        this.account = AccountDto(transaction.account)
        this.category = transaction.category?.let { TypedCategoryDto(it) }
        this.import = transaction.import?.let { ImportDto(it) }
        this.owner = UserDto(transaction.user)
        this.date = transaction.date
        this.notes = transaction.notes
        this.longitude = transaction.longitude
        this.latitude = transaction.latitude
        this.images = transaction.attachments.map { TransactionImageDto(id, it) }
    }
}