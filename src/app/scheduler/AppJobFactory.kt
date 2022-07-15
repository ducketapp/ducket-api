package io.ducket.api.app.scheduler

import io.ducket.api.clients.rates.ReferenceRatesClient
import io.ducket.api.domain.service.CurrencyService
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
import kotlin.reflect.jvm.jvmName

class AppJobFactory(
    private val currencyRatesClient: ReferenceRatesClient,
    private val currencyService: CurrencyService,
): JobFactory {

    override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        if (bundle != null) {
            val jobClass = bundle.jobDetail.jobClass
            if (jobClass.name == AppCurrencyRatesPullJob::class.jvmName) {
                return AppCurrencyRatesPullJob(currencyRatesClient, currencyService)
            }
        }
        throw NotImplementedError("Job Factory error")
    }
}