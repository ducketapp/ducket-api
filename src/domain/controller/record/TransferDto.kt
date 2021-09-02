package io.budgery.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.budgery.api.domain.controller.account.AccountDto
import io.budgery.api.domain.model.transfer.Transfer

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TransferDto(@JsonIgnore val transfer: Transfer): RecordDto(transfer) {
    val transferAccount: AccountDto = AccountDto(transfer.transferAccount)
    val exchangeRate: Double = transfer.exchangeRate
    val relationUuid: String = transfer.relationUuid.toString()
}