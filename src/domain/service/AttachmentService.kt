package io.budgery.api.domain.service

import io.budgery.api.domain.controller.record.AttachmentDto
import io.ktor.http.*
import io.ktor.http.content.*
import java.io.File
import java.time.Instant

abstract class AttachmentService(
    private val attachmentsLimit: Int,
) {

    abstract fun getAttachmentFile(userId: Int, entityId: Int, attachmentId: Int): File

    abstract fun addAttachments(userId: Int, entityId: Int, multipartData: List<PartData>)

    protected fun retrieveOriginalFiles(multipartData: List<PartData>, actualAmount: Int): List<Pair<File, ByteArray>> {
        val result: List<Pair<File, ByteArray>> = multipartData.mapIndexed { idx, part ->
            if (part is PartData.FileItem) {
                if (part.name == "file") {
                    val contentType = part.headers[HttpHeaders.ContentType]
                    val fileName = part.originalFileName
                    val fileExtension = fileName?.split(".")?.last()
                    val fileBytes = part.streamProvider().readBytes()
                    val fileSize = bytesToMegabytes(fileBytes)

                    if (contentType?.startsWith("image/") == false) {
                        throw IllegalArgumentException("Unsupported mime type: $contentType")
                    }

                    if (fileName == null || fileExtension == null) {
                        throw IllegalArgumentException("Wrong '$fileName' file name at index: $idx")
                    }

                    if (fileSize == 0.0 || fileSize > 1.0) {
                        throw IllegalArgumentException("Unsupported file size, limit is 1MB")
                    }

                    return@mapIndexed Pair(File(fileName), fileBytes)
                } else {
                    throw IllegalArgumentException("Wrong multipart key name at index: $idx")
                }
            } else {
                throw IllegalArgumentException("Unsupported part data at index: $idx")
            }
        }

        if (result.size + actualAmount > attachmentsLimit) {
            throw IllegalArgumentException("Attachments limit exceeded, $attachmentsLimit max")
        }

        return result
    }

    protected fun getLocalFile(filePath: String): File? {
        val file = File(filePath)
        return if (file.exists()) file else null
    }

    protected fun createLocalFile(originalFile: File, originalFileContent: ByteArray, postFix: String): File {
        val newFileName = "${Instant.now().toEpochMilli()}_$postFix.${originalFile.extension}"
        val newFile = File("resources/uploads/$newFileName")
        newFile.writeBytes(originalFileContent)

        return newFile
    }

    private fun bytesToMegabytes(byteArray: ByteArray): Double {
        return (byteArray.size.toDouble() / 1024) / 1024
    }
}