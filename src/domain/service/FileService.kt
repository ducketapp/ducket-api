package io.ducket.api.domain.service

import io.ducket.api.config.AppConfig
import io.ducket.api.config.DatabaseConfig
import io.ducket.api.getLogger
import io.ducket.api.plugins.InvalidDataError
import io.ktor.http.*
import io.ktor.http.content.*
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.nio.file.Paths
import java.time.Instant

abstract class FileService {
    private val config: AppConfig by inject(AppConfig::class.java)
    private val logger = getLogger()

    protected fun extractImportData(multipartData: List<PartData>): Pair<File, ByteArray> {
        if (multipartData.size == 1) {
            val partData = multipartData[0]

            if (partData is PartData.FileItem && partData.name == "file") {
                val fileName = partData.originalFileName
                val fileExtension = fileName?.split(".")?.last()
                val fileBytes = partData.streamProvider().readBytes()

                if (fileName == null || fileExtension == null || fileExtension != "csv") {
                    throw InvalidDataError("Unsupported file, expected *.csv")
                }

                return Pair(File(fileName), fileBytes)
            } else {
                throw InvalidDataError("Unsupported part data, expected a file")
            }
        } else {
            throw InvalidDataError("Unsupported amount of files, expected 1")
        }
    }

    protected fun extractImagesData(multipartData: List<PartData>): List<Pair<File, ByteArray>> {
        val result = multipartData.mapIndexed { idx, part ->
            if (part is PartData.FileItem) {
                if (part.name == "file") {
                    val contentType = part.headers[HttpHeaders.ContentType]
                    val fileName = part.originalFileName
                    val fileExtension = fileName?.split(".")?.last()
                    val fileBytes = part.streamProvider().readBytes()
                    val fileSize = bytesToMegabytes(fileBytes)

                    if (contentType?.startsWith("image/") == false) {
                        throw InvalidDataError("Unsupported mime type: $contentType")
                    }

                    if (fileName == null || fileExtension == null) {
                        throw InvalidDataError("Invalid '$fileName' file name at index: $idx")
                    }

                    if (fileSize == 0.0 || fileSize > 1.0) {
                        throw InvalidDataError("Unsupported file size, limit is 1MB")
                    }

                    return@mapIndexed Pair(File(fileName), fileBytes)
                } else {
                    throw InvalidDataError("Invalid multipart key name at index: $idx")
                }
            } else {
                throw InvalidDataError("Unsupported part data at index: $idx")
            }
        }

        if (result.isEmpty()) {
            throw InvalidDataError("At least one attachment required!")
        }

        return result
    }

    protected fun getLocalFile(filePath: String): File? {
        return File(filePath).takeIf { it.exists() }
    }

    protected fun deleteLocalFile(filePath: String): Boolean {
        try {
            getLocalFile(filePath)?.let {
                logger.debug("Delete local file: $filePath")
                it.delete()
                return true
            }
        } catch (e: Exception) {
            logger.error("Cannot delete local file $filePath: ${e.message}")
        }
        return false
    }

    protected fun createLocalAttachmentFile(extension: String, content: ByteArray): File {
        return createLocalFile("images", extension, content)
    }

    protected fun createLocalImportFile(extension: String, content: ByteArray): File {
        return createLocalFile("imports", extension, content)
    }

    private fun createLocalFile(dir: String, extension: String, content: ByteArray): File {
        val fileName = "${Instant.now().toEpochMilli()}.$extension"
        val filePath = Paths.get(config.databaseConfig.dataPath, dir, fileName)
        val localFile = File(filePath.toUri())

        logger.debug("Create local file: ${localFile.path}")

        localFile.parentFile.mkdirs()
        localFile.writeBytes(content)

        return localFile
    }

    private fun bytesToMegabytes(byteArray: ByteArray): Double {
        return (byteArray.size.toDouble() / 1024) / 1024
    }
}