package io.ducket.api.domain.controller.ledger

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.utils.InstantSerializer
import io.ducket.api.app.LedgerRecordType
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.model.ledger.LedgerRecord
import java.math.BigDecimal
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LedgerRecordDto(
    val id: Long,
    val transferAccount: AccountDto?,
    val account: AccountDto,
    val operation: OperationDto,
    val type: LedgerRecordType,
    val amountPosted: BigDecimal,
    val amountTransferred: BigDecimal,
    val balance: BigDecimal,
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant,
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant,
) {
    constructor(ledgerRecord: LedgerRecord, balance: BigDecimal): this(
        id = ledgerRecord.id,
        transferAccount = ledgerRecord.transferAccount?.let { AccountDto(it) },
        account = AccountDto(ledgerRecord.account),
        operation = OperationDto(ledgerRecord.operation),
        type = ledgerRecord.type,
        amountPosted = ledgerRecord.amountPosted,
        amountTransferred = ledgerRecord.amountTransferred,
        balance = balance,
        createdAt = ledgerRecord.createdAt,
        modifiedAt = ledgerRecord.modifiedAt,
    )
}
