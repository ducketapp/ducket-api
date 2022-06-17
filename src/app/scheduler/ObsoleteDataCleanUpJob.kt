package io.ducket.api.app.scheduler

import io.ducket.api.domain.repository.AttachmentRepository
import io.ducket.api.domain.service.LocalFileService
import io.ducket.api.getLogger
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.io.File
import java.lang.Exception

open class ObsoleteDataCleanUpJob(private val attachmentRepository: AttachmentRepository): Job {

    override fun execute(context: JobExecutionContext?) {
        if (context == null) return

        try {
            val databaseStoreDataPath = context.jobDetail.jobDataMap.getString(JOB_DATA_PATH_KEY)
            val databaseDataPaths = attachmentRepository.findAllPaths()

            File(databaseStoreDataPath).walkTopDown().forEach {
                if (it.isFile && it.name.startsWith(LocalFileService.LOCAL_FILE_PREFIX) && !databaseDataPaths.contains(it.absolutePath)) {
                    getLogger().info("Obsolete file was found, ${it.parent}/${it.name}. Cleaning up...")
                    it.delete()
                }
            }
        } catch (e: Exception) {
            getLogger().error("Cannot execute the command: ${e.localizedMessage}")
        }
    }

    companion object {
        const val JOB_DATA_PATH_KEY = "data_path"
    }
}