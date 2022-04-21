package io.ducket.api

import io.ducket.api.app.database.AppDatabaseFactory
import io.ducket.api.app.database.DatabaseFactory
import io.ducket.api.config.AppConfig
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.service.LocalFileService.Companion.LOCAL_FILE_PREFIX
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent
import java.io.File
import java.lang.Exception
import java.util.concurrent.Executors
import javax.activation.MimetypesFileTypeMap
import kotlin.coroutines.CoroutineContext

class DatabaseFilesCleanUpScheduler(val interval: Long, val startDelay: Long? = null): CoroutineScope {
    override val coroutineContext: CoroutineContext get() = job + singleThreadExecutor.asCoroutineDispatcher()

    private val logger = getLogger()
    private val appDatabaseFactory: AppDatabaseFactory by KoinJavaComponent.inject(DatabaseFactory::class.java)
    private val appConfig: AppConfig by KoinJavaComponent.inject(AppConfig::class.java)

    private val job = Job()
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()

    fun stop() {
        job.cancel()
        singleThreadExecutor.shutdown()
    }

    fun start() = launch {
        val dbDataPath = appConfig.localDataConfig.dbDataPath

        startDelay?.let {
            delay(it)
        }

        while (isActive) {
            try {
                val dbFilePaths = appDatabaseFactory.dbTransaction { AttachmentEntity.all().map { it.filePath } }

                File(dbDataPath).walkTopDown().forEach {
                    if (it.isFile && it.name.startsWith(LOCAL_FILE_PREFIX) && !dbFilePaths.contains(it.absolutePath)) {
                        logger.info("Obsolete file was found, ${it.parent}/${it.name}. Cleaning up...")
                        it.delete()
                    }
                }
            } catch (e: Exception) {
                logger.error("Cannot execute the command: ${e.localizedMessage}")
            }

            delay(interval)
        }
    }

    private fun isImageFile(filePath: String): Boolean {
        return MimetypesFileTypeMap().getContentType(filePath).split("/")[0] == "image"
    }

    private fun isCsvFile(fileExtension: String): Boolean {
        return fileExtension == "csv"
    }
}