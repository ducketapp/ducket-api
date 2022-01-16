package io.ducket.api.domain.controller.transfer

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.record.RecordDto
import io.ducket.api.domain.model.transfer.Transfer
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TransferDto(@JsonIgnore val transfer: Transfer): RecordDto(transfer) {
    val transferAccount: AccountDto = AccountDto(transfer.transferAccount)
    val exchangeRate: BigDecimal = transfer.exchangeRate
    val relationCode: String? = transfer.relationCode
}