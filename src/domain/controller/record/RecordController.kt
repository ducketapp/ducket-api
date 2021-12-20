package io.ducket.api.domain.controller.record

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.AccountService
import io.ducket.api.domain.service.TransactionService
import io.ducket.api.domain.service.TransferService
import io.ducket.api.domain.service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*

class RecordController(
    val transactionService: TransactionService,
    val transferService: TransferService,
    val accountService: AccountService,
) {

    suspend fun getRecords(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        val allRecords = listOf(
            transactionService.getTransactionsAccessibleToUser(userId),
            transferService.getTransfersAccessibleToUser(userId),
        ).flatten().sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.id })

//        val followedUsersRecords = userService.getUserFollowings(userId).map {
//            getRecordsByOwner(it.followedUser.id)
//        }.flatten()
//
//        val allRecords = getRecordsByOwner(userId).plus(followedUsersRecords)
//            .sortedWith(compareByDescending<RecordDto> { it.date }.thenByDescending { it.id })

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

        ctx.respond(HttpStatusCode.OK, allRecords)
    }
}