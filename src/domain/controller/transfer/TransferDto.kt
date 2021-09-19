package io.budgery.api.domain.controller.transfer

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.budgery.api.domain.controller.account.AccountDto
import io.budgery.api.domain.controller.record.RecordDto
import io.budgery.api.domain.model.transfer.Transfer
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TransferDto(@JsonIgnore val transfer: Transfer): RecordDto(transfer) {
    val transferAccount: AccountDto = AccountDto(transfer.transferAccount)
    val exchangeRate: BigDecimal = transfer.exchangeRate
    val relationUuid: String = transfer.relationUuid.toString()
}