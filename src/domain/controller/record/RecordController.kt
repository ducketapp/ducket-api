package io.ducket.api.domain.controller.record

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.TransactionService
import io.ducket.api.domain.service.TransferService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import kotlin.streams.toList

class RecordController(
    val transactionService: TransactionService,
    val transferService: TransferService,
    val accountService: AccountService,
) {

    suspend fun getUserRecords(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        // get all user transactions
        val transactions = transactionService.getTransactions(userId).stream().peek {
            it.account.balance = accountService.resolveBalance(userId, it.account.id, it.date)
        }.toList()

        // get all user transfers
        val transfers = transferService.getTransfers(userId).stream().peek {
            it.account.balance = accountService.resolveBalance(userId, it.account.id, it.date)
            it.transferAccount.balance = accountService.resolveBalance(userId, it.transferAccount.id, it.date)
        }.toList()

        var totalRecords = listOf(transactions, transfers)
            .flatten()
            .sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.amount })

        /*ctx.request.queryParameters["startDate"]?.let { queryParam ->
            val startDate = LocalDate.parse(queryParam, DateTimeFormatter.ISO_LOCAL_DATE)
            totalRecords = totalRecords.filter { it.date.isAfterInclusive(startDate) }
        }

        ctx.request.queryParameters["endDate"]?.let { queryParam ->
            val endDate = LocalDate.parse(queryParam, DateTimeFormatter.ISO_LOCAL_DATE)
            totalRecords = totalRecords.filter { it.date.isBeforeInclusive(endDate) }
        }

        ctx.request.queryParameters["order"]?.let { queryParam ->
            totalRecords = when (queryParam) {
                "asc" -> totalRecords.sortedBy { LocalDateTime.of(it.date, it.time) }
                "desc" -> totalRecords.sortedByDescending { LocalDateTime.of(it.date, it.time) }
                else -> throw Exception("Unknown records order type: $queryParam")
            }
        }*/

        ctx.respond(HttpStatusCode.OK, totalRecords)
    }
}