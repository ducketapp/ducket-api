package io.ducket.api.domain.controller.ledger

import com.fasterxml.jackson.annotation.JsonInclude
import io.ducket.api.app.LedgerRecordType
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.model.ledger.LedgerRecord
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LedgerRecordDto(
    val id: Long,
    val transferAccount: AccountDto?,
    val account: AccountDto,
    val operation: OperationDto,
    val type: LedgerRecordType,
    val clearedFunds: BigDecimal,
    val postedFunds: BigDecimal,
) {
    constructor(ledgerRecord: LedgerRecord): this(
        id = ledgerRecord.id,
        transferAccount = ledgerRecord.transferAccount?.let { AccountDto(it) },
        account = AccountDto(ledgerRecord.account),
        operation = OperationDto(ledgerRecord.operation),
        type = ledgerRecord.type,
        clearedFunds = ledgerRecord.clearedFunds,
        postedFunds = ledgerRecord.postedFunds,
    )
}
