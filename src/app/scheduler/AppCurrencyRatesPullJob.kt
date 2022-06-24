package io.ducket.api.app.scheduler

import clients.rates.ReferenceRatesClient
import io.ducket.api.domain.service.CurrencyService
import io.ducket.api.getLogger
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.lang.Exception

open class AppCurrencyRatesPullJob(
    private val currencyRatesClient: ReferenceRatesClient,
    private val currencyService: CurrencyService,
): Job {
    private val logger = getLogger()

    override fun execute(context: JobExecutionContext?) {
        if (context == null) return

        try {
            runBlocking {
                val currencies = currencyService.getCurrencies().map { it.isoCode }
                val latestResult = currencyRatesClient.getLatest(*currencies.toTypedArray())

                currencyService.putCurrencyRates(latestResult.dataSet.references, latestResult.header.sender.id)
            }
        } catch (e: Exception) {
            logger.error("Cannot execute the command: ${e.localizedMessage}")
        }
    }
}