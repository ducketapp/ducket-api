package io.ducket.api

import io.ducket.api.app.database.MainDatabase
import io.ducket.api.app.di.AppModule
import io.ducket.api.config.AppConfig
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.service.LocalFileService.Companion.LOCAL_FILE_PREFIX
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.lang.Exception
import java.util.concurrent.Executors
import javax.activation.MimetypesFileTypeMap
import kotlin.coroutines.CoroutineContext

class DatabaseFilesCleanUpScheduler(
    val interval: Long,
    val startDelay: Long? = null
): CoroutineScope, KoinComponent {
    override val coroutineContext: CoroutineContext get() = job + singleThreadExecutor.asCoroutineDispatcher()

    private val db: MainDatabase by inject(named(AppModule.DatabaseType.MAIN_DB))
    private val appConfig: AppConfig by inject()

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
                val dbFilePaths = db.doTransaction { AttachmentEntity.all().map { it.filePath } }

                File(dbDataPath).walkTopDown().forEach {
                    if (it.isFile && it.name.startsWith(LOCAL_FILE_PREFIX) && !dbFilePaths.contains(it.absolutePath)) {
                        getLogger().info("Obsolete file was found, ${it.parent}/${it.name}. Cleaning up...")
                        it.delete()
                    }
                }
            } catch (e: Exception) {
                getLogger().error("Cannot execute the command: ${e.localizedMessage}")
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