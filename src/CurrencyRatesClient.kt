package io.ducket.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Paths
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


class CurrencyRatesClient {
    private val logger = getLogger()
    private val sourceUrl = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
    private val downloadPath = Paths.get(System.getProperty("java.io.tmpdir"), "ecb")
    private val fileNamePrefix = "ecb_exr"

    private var client: HttpClient = HttpClient(CIO) {
        engine {
            requestTimeout = 25000
        }
    }

    var xmlMapper: XmlMapper = XmlMapper(
        JacksonXmlModule().apply { setDefaultUseWrapper(false) }
    ).apply {
        enable(SerializationFeature.INDENT_OUTPUT)
        enable(SerializationFeature.WRAP_ROOT_VALUE)
    }

    @Throws(InvalidCurrencyException::class)
    fun getCurrencyRate(base: String, term: String): BigDecimal {
        logger.debug("Getting currency rate: $base -> $term")

        val fileWithRates = pullRates()
        val currencyToRateMap = parseXml(fileWithRates)

        if (currencyToRateMap.keys.containsAll(listOf(base, term))) {
            val baseRate = currencyToRateMap[base]!!
            val termRate = currencyToRateMap[term]!!

            logger.debug("Rates: base=$baseRate, term=$termRate")

            return termRate / baseRate
        } else {
            throw InvalidCurrencyException(currencyToRateMap.keys.toList())
        }
    }

    @Throws(CurrencyRateParsingException::class)
    fun getRatesMap(): Map<String, BigDecimal> {
        logger.debug("Getting rates map")

        val fileWithRates = pullRates()
        return parseXml(fileWithRates)
    }

    @Throws(CurrencyRatePullException::class)
    fun pullRates(): File {
        logger.debug("Resolving currencies rates file")

        val todayRatesFileName = "${fileNamePrefix}_${LocalDate.now()}.xml"
        val downloadedRates = File(downloadPath.toUri()).listFiles()
            ?.filter { it.isFile && it.name.startsWith(fileNamePrefix) }
            ?.sortedByDescending { it.name }

        var todayRatesFile = downloadedRates?.firstOrNull { it.name == todayRatesFileName }

        if (todayRatesFile == null) {
            logger.debug("The file with actual currency rates was not found at $downloadPath")

            todayRatesFile = File(Paths.get(downloadPath.toString(), todayRatesFileName).toUri())

            return runBlocking {
                try {
                    logger.info("Pulling an actual currency rates from $sourceUrl")
                    val response: HttpResponse = client.get(sourceUrl)

                    if (response.status == HttpStatusCode.OK) {
                        todayRatesFile.parentFile.mkdirs()
                        todayRatesFile.writeBytes(response.receive())

                        logger.debug("The file with actual currency rates was created: ${todayRatesFile.path}")
                        return@runBlocking todayRatesFile
                    } else {
                        throw Exception()
                    }
                } catch (e: Exception) {
                    logger.error("Error in pulling currency rates from $sourceUrl: ${e.message}")

                    if (downloadedRates != null && downloadedRates.isNotEmpty()) {
                        val lastDownloadedFile: File =  downloadedRates[0]
                        logger.info("Using last file: ${lastDownloadedFile.path}")

                        return@runBlocking lastDownloadedFile
                    } else {
                        throw CurrencyRatePullException(e)
                    }
                }
            }
        } else {
            logger.debug("Using recently pulled file: ${todayRatesFile.path}")
            return todayRatesFile
        }
    }

    @Throws(CurrencyRateParsingException::class)
    private fun parseXml(xmlFile: File): Map<String, BigDecimal> {
        logger.debug("Parsing currencies rates from ${xmlFile.path}")

        val currenciesNodeList: NodeList
        val currenciesExpression = "/Envelope/Cube[1]/Cube[1]/Cube"

        try {
            val xmlDocBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val xmlDoc: Document = xmlDocBuilder.parse(FileInputStream(xmlFile))
            val xPath: XPath = XPathFactory.newInstance().newXPath()

            logger.debug("Getting the list of currencies by '$currenciesExpression' xPath")
            currenciesNodeList = xPath.compile(currenciesExpression).evaluate(xmlDoc, XPathConstants.NODESET) as NodeList
        } catch (e: Exception) {
            throw CurrencyRateParsingException(e)
        }

        val currenciesMap = getCurrenciesMap(currenciesNodeList)
        logger.debug("${currenciesMap.size} currencies parsed: $currenciesMap")

        return currenciesMap
    }

    private fun getCurrenciesMap(currencies: NodeList): Map<String, BigDecimal> {
        logger.debug("Getting currencies map")

        val map = HashMap<String, BigDecimal>()
        map["EUR"] = BigDecimal.ONE

        for (i in 0 until currencies.length) {
            try {
                val currency: String = currencies.item(i).attributes.getNamedItem("currency").nodeValue
                val rate: String = currencies.item(i).attributes.getNamedItem("rate").nodeValue

                map[currency] = rate.toBigDecimal()
            } catch (e: Exception) {
                logger.error("Cannot parse currency at $i index: ${e.message}")
            }
        }

        map.entries.map {
            map[it.key] = it.value.setScale(4, RoundingMode.HALF_UP)
        }

        return map
    }

    class CurrencyRateClientException(message: String): Exception(message)
    class CurrencyRatePullException(cause: Throwable, message: String = "Cannot pull rates data: ${cause.message}") : Exception(message)
    class CurrencyRateParsingException(cause: Throwable, message: String = "Cannot parse rates data: ${cause.message}") : Exception(message)
    class InvalidCurrencyException(supported: List<String>, message: String = "Invalid currency, supported: $supported") : Exception(message)
}