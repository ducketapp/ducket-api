package io.ducket.api.clients.rates

import io.ktor.client.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent
import java.time.LocalDate


/**
 * ECB documentation: https://sdw-wsrest.ecb.europa.eu/help/
 */
class ReferenceRatesClient(private val httpClient: HttpClient) : KoinComponent {
    private val responseDataFormat: String = "structurespecificdata"
    private val baseCurrency: String = "EUR"

    suspend fun getAll(vararg currencies: String): DataStructureDto {
        return httpClient.get(getUrlPath(*currencies)) {
            parameter("format", responseDataFormat)
        }
    }

    suspend fun getFirstStartingFromDate(currency: String, startDate: LocalDate): DataStructureDto {
        return httpClient.get(getUrlPath(currency)) {
            parameter("format", responseDataFormat)
            parameter("firstNObservations", "1")
            parameter("startPeriod", startDate.toString())
        }
    }

    suspend fun getLatest(vararg currencies: String): DataStructureDto {
        return httpClient.get(getUrlPath(*currencies)) {
            parameter("format", responseDataFormat)
            parameter("lastNObservations", "1")
        }
    }

    private fun getUrlPath(vararg currencies: String): String {
        return "D.${currencies.joinToString(separator = "+").uppercase()}.${baseCurrency}.SP00.A"
    }
}