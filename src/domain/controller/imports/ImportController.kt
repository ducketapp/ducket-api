package dev.ducketapp.service.domain.controller.imports

import dev.ducketapp.service.domain.controller.imports.dto.ImportUpdateDto
import dev.ducketapp.service.domain.controller.imports.dto.OperationImportDto
import dev.ducketapp.service.domain.service.ImportService
import dev.ducketapp.service.plugins.InvalidDataException
import dev.ducketapp.service.principalOrThrow
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.util.*
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toListOf
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.valiktor.ConstraintViolationException
import java.io.File

class ImportController(private val importService: ImportService) {

    suspend fun getImports(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        importService.getImports(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun getImport(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importId = ctx.parameters.getOrFail("importId").toLong()

        importService.getImport(userId, importId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun updateImport(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importId = ctx.parameters.getOrFail("importId").toLong()

        ctx.receive<ImportUpdateDto>().let { reqObj ->
            importService.updateImport(userId, importId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
            }
        }
    }

    suspend fun deleteImport(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val importId = ctx.parameters.getOrFail("importId").toLong()

        importService.deleteImport(userId, importId)
        ctx.respond(HttpStatusCode.NoContent)
    }

    suspend fun createImport(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        var accountId = -1L
        var fileName = ""
        var fileContent = ByteArray(0)

        ctx.receiveMultipart().readAllParts().also { dataList ->
            if (dataList.isEmpty()) throw InvalidDataException("Form data cannot be empty")

            dataList.forEach { data ->
                when (data) {
                    is PartData.FormItem -> {
                        if (data.name == "accountId") {
                            accountId = data.value.toLong()
                        }
                    }
                    is PartData.FileItem -> {
                        if (data.name == "file") {
                            fileName = data.originalFileName as String
                            fileContent = data.streamProvider().readBytes()

                            if (fileName.let { File(it).extension } != "csv") {
                                throw InvalidDataException("Unsupported file type, expected .csv")
                            }
                        }
                    }
                    else -> throw InvalidDataException("Unsupported form data type")
                }
            }
        }

        if (fileContent.isEmpty()) throw InvalidDataException("File cannot be empty")

        val mappedFileContent = DataFrame.readCSV(fileContent.inputStream())
            .toListOf<OperationImportDto>()
            .also {
                it.forEachIndexed { i, dto ->
                    try {
                        dto.validate()
                    } catch (e: ConstraintViolationException) {
                        throw InvalidDataException("Invalid data at row #$i: ${e.localizedMessage}")
                    }
                }
            }

        importService.createImport(userId, accountId, fileName, mappedFileContent).let { resObj ->
            ctx.respond(HttpStatusCode.Created, resObj)
        }
    }
}