package dev.ducketapp.service.plugins

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import dev.ducketapp.service.getLogger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.*
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import java.sql.BatchUpdateException
import java.time.Instant
import java.util.*

fun Application.installErrorHandlingPlugin() {
    install(StatusPages) {
        applicationStatuses()
        applicationExceptions()
    }
}

fun StatusPagesConfig.applicationStatuses() {
    status(HttpStatusCode.NotFound) { cause ->
        call.respond(cause, ErrorResponse(cause, "Route not found"))
    }

    status(HttpStatusCode.Unauthorized) { cause ->
        call.respond(cause, ErrorResponse(cause, cause.description))
    }
}

@OptIn(InternalAPI::class)
fun StatusPagesConfig.applicationExceptions() {
    exception<Throwable> { call, cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.also {
            call.respond(status = it, message = ErrorResponse(it, "Oops, something went wrong!"))
        }
    }

    exception<ConstraintViolationException> { call, cause ->
        val errors = cause.constraintViolations
            .mapToMessage("messages", Locale.ENGLISH)
            .map { err -> "The '${err.property}' field ${err.message.lowercase()}" }

        HttpStatusCode.PreconditionFailed.also {
            call.respond(status = it, message = ErrorResponse(it, errors[0]))
        }
    }

    exception<AuthenticationException> { call, cause ->
        HttpStatusCode.Unauthorized.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<AuthorizationException> {  call, cause ->
        HttpStatusCode.Forbidden.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<UnrecognizedPropertyException> {  call, cause ->
        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, "Unrecognized property: '${cause.propertyName}'"))
        }
    }

    exception<BadRequestException> { call, cause ->
        val rootCause = cause.rootCause

        val message = if (rootCause is MissingKotlinParameterException) {
            "Required field '${rootCause.parameter.name}' is missing"
        } else {
            getLogger().error(cause.stackTraceToString())
            "Incorrect client request"
        }

        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, message))
        }
    }

    exception<MissingKotlinParameterException> {  call, cause ->
        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, "Required field '${cause.parameter.name}' is missing"))
        }
    }

    exception<InvalidDataException> {  call, cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<DuplicateDataException> {  call, cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<BatchUpdateException> {  call, cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, DuplicateDataException().localizedMessage))
        }
    }

    exception<BusinessLogicException> {  call, cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<UnexpectedBehaviourException> {  call, cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<NoDataFoundException> {  call, cause ->
        HttpStatusCode.NotFound.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }
}

class AuthenticationException(message: String = "Authentication failure") : Exception(message)
class AuthorizationException(message: String = "Access denied") : Exception(message)
class NoDataFoundException(message: String = "No such entity was found") : Exception(message)
class DuplicateDataException(message: String = "Such an entity already exists") : Exception(message)
class InvalidDataException(message: String = "Invalid data") : Exception(message)
class UnexpectedBehaviourException(message: String = "Unexpected behaviour exception occurred") : Exception(message)
class BusinessLogicException(message: String = "Exception in business logic") : Exception(message)

data class ErrorResponse(
    val status: HttpStatusCode,
    val message: String,
    val timestamp: String? = Instant.now().toString()
)