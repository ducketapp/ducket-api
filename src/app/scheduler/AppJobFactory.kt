package io.ducket.api.app.scheduler

import io.ducket.api.domain.repository.AttachmentRepository
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
import kotlin.reflect.jvm.jvmName

class AppJobFactory(
    private val attachmentRepository: AttachmentRepository
): JobFactory {

    override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        if (bundle != null) {
            val jobClass = bundle.jobDetail.jobClass
            if (jobClass.name == ObsoleteDataCleanUpJob::class.jvmName) {
                return ObsoleteDataCleanUpJob(attachmentRepository)
            }
        }
        throw NotImplementedError("Job Factory error")
    }
}