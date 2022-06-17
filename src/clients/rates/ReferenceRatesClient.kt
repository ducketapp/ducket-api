package clients.rates

import io.ducket.api.clients.rates.DataStructureDto
import io.ktor.client.*
import io.ktor.client.request.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.koin.core.component.KoinComponent
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.time.LocalDate

data class ExrRecordDto(
    val quote: String,
    val base: String,
    val value: BigDecimal,
    val date: LocalDate,
    val source: String = "European Central Bank (ECB)",
)


// https://sdw-wsrest.ecb.europa.eu/help/
class ReferenceRatesClient(private val httpClient: HttpClient) : KoinComponent {
    private val format: String = "structurespecificdata"

    suspend fun getLatestReferenceRatesData(currency: String, baseCurrency: String): DataStructureDto {
        return httpClient.get(getUrlPath(currency)) {
            parameter("format", format)
            parameter("lastNObservations", "1")
        }
    }

    suspend fun getFirstRateStartingFromDate(currency: String, baseCurrency: String, startDate: LocalDate): DataStructureDto {
        return httpClient.get(getUrlPath(currency)) {
            parameter("format", format)
            parameter("firstNObservations", "1")
            parameter("startPeriod", startDate.toString())
        }
    }

    suspend fun getAllRates(vararg currencies: String): DataStructureDto {
        return httpClient.get(getUrlPath(*currencies)) {
            parameter("format", format)
        }
    }

    private fun getUrlPath(vararg currencies: String): String {
        return "D.${currencies.joinToString(separator = "+").uppercase()}.EUR.SP00.A"
    }

    private fun mapToDto(csvRecord: CSVRecord): ExrRecordDto {
        return ExrRecordDto(
            quote = csvRecord["CURRENCY"],
            base = csvRecord["CURRENCY_DENOM"],
            value = csvRecord["OBS_VALUE"].toBigDecimal(),
            date = LocalDate.parse(csvRecord["TIME_PERIOD"]),
        )
    }

    private fun getDefaultCsvParser(inputStream: InputStream): CSVParser {
        val reader = BufferedReader(InputStreamReader(inputStream))
        return CSVParser(
            reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withDelimiter(',')
                .withIgnoreHeaderCase()
                .withTrim()
        )
    }
}