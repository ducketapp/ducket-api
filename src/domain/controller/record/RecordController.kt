package io.budgery.api.domain.controller.record

import io.budgery.api.config.JwtConfig
import io.budgery.api.config.UserPrincipal
import io.budgery.api.domain.service.AccountService
import io.budgery.api.domain.service.TransactionService
import io.budgery.api.domain.service.TransferService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import kotlin.streams.toList

class RecordController(
    val transactionService: TransactionService,
    val transferService: TransferService,
    val accountService: AccountService,
) {

    suspend fun addManualTransfer(ctx: ApplicationCall, user: UserPrincipal) {
        ctx.receive<TransferCreateDto>().apply {
            transferService.addTransfer(user.id, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun addManualTransaction(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<TransactionCreateDto>().apply {
            transactionService.addTransaction(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getUserRecords(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        // get all user transactions
        val transactions = transactionService.getTransactions(userId).stream().peek {
            // set account total amount
            it.account.balance = accountService.getAmount(userId, it.account.id, it.date)
        }.toList()

        // get all user transfers
        val transfers = transferService.getTransfers(userId).stream().peek {
            // set accounts balance
            it.account.balance = accountService.getAmount(userId, it.account.id, it.date)
            it.transferAccount.balance = accountService.getAmount(userId, it.transferAccount.id, it.date)
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

    suspend fun getTransaction(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transactionId = ctx.parameters.getOrFail("transactionId").toInt()

        val account = transactionService.getTransaction(userId, transactionId)
        ctx.respond(HttpStatusCode.OK, account)
    }

    suspend fun deleteTransaction(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transactionId = ctx.parameters.getOrFail("transactionId").toInt()

        if (transactionService.deleteTransaction(userId, transactionId)) ctx.respond(HttpStatusCode.NoContent)
        else ctx.respond(HttpStatusCode.UnprocessableEntity)
    }

    suspend fun deleteTransfer(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id
        val transferId = ctx.parameters.getOrFail("transferId").toInt()

        if (transferService.deleteTransfer(userId, transferId)) ctx.respond(HttpStatusCode.NoContent)
        else ctx.respond(HttpStatusCode.UnprocessableEntity)
    }
}