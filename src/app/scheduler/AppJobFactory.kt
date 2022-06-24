package io.ducket.api.app.scheduler

import clients.rates.ReferenceRatesClient
import io.ducket.api.domain.repository.AttachmentRepository
import io.ducket.api.domain.service.CurrencyService
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
import kotlin.reflect.jvm.jvmName

class AppJobFactory(
    private val attachmentRepository: AttachmentRepository,
    private val currencyRatesClient: ReferenceRatesClient,
    private val currencyService: CurrencyService,
): JobFactory {

    override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        if (bundle != null) {
            val jobClass = bundle.jobDetail.jobClass
            if (jobClass.name == AppObsoleteDataCleanUpJob::class.jvmName) {
                return AppObsoleteDataCleanUpJob(attachmentRepository)
            }
            if (jobClass.name == AppCurrencyRatesPullJob::class.jvmName) {
                return AppCurrencyRatesPullJob(currencyRatesClient, currencyService)
            }
        }
        throw NotImplementedError("Job Factory error")
    }
}