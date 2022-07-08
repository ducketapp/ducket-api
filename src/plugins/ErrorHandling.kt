package io.ducket.api.plugins

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ducket.api.getLogger
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import java.sql.BatchUpdateException
import java.time.Instant
import java.util.*

fun Application.installErrorHandling() {
    install(StatusPages) {
        applicationStatuses()
        applicationExceptions()
    }
}

fun StatusPages.Configuration.applicationStatuses() {
    status(HttpStatusCode.NotFound) { cause ->
        call.respond(cause, ErrorResponse(cause, "Route not found"))
    }

    status(HttpStatusCode.Unauthorized) { cause ->
        call.respond(cause, ErrorResponse(cause, cause.description))
    }
}

fun StatusPages.Configuration.applicationExceptions() {

    exception<Throwable> { cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.also {
            call.respond(status = it, message = ErrorResponse(it, "Oops, something went wrong!"))
        }
    }

    exception<ConstraintViolationException> { cause ->
        val errors = cause.constraintViolations
            .mapToMessage("messages", Locale.ENGLISH)
            .map { err -> "The '${err.property}' field ${err.message.lowercase()}" }

        HttpStatusCode.PreconditionFailed.also {
            call.respond(status = it, message = ErrorResponse(it, errors[0]))
        }
    }

    exception<AuthenticationException> { cause ->
        HttpStatusCode.Unauthorized.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<AuthorizationException> { cause ->
        HttpStatusCode.Forbidden.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<UnrecognizedPropertyException> { cause ->
        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, "Unrecognized property: '${cause.propertyName}'"))
        }
    }

    exception<MissingKotlinParameterException> { cause ->
        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, "Required field '${cause.parameter.name}' is missing"))
        }
    }

    exception<InvalidDataException> { cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<DuplicateDataException> { cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<BatchUpdateException> { cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.also {
            call.respond(status = it, message = ErrorResponse(it, DuplicateDataException().localizedMessage))
        }
    }

    exception<BusinessLogicException> { cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<UnexpectedBehaviourException> { cause ->
        getLogger().error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.also {
            call.respond(status = it, message = ErrorResponse(it, cause.localizedMessage))
        }
    }

    exception<NoDataFoundException> { cause ->
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