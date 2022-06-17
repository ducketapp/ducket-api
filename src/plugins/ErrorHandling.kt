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
        HttpStatusCode.InternalServerError.apply {
            call.respond(this, ErrorResponse(this, "Oops, something went wrong!"))
        }
    }

    exception<ConstraintViolationException> { cause ->
        HttpStatusCode.PreconditionFailed.apply {
            val errors = cause.constraintViolations
                .mapToMessage("messages", Locale.ENGLISH)
                .map { "The '${it.property}' field ${it.message.lowercase()}" }

            call.respond(this, ErrorResponse(this, errors[0]))
        }
    }

    exception<AuthenticationException> { cause ->
        HttpStatusCode.Unauthorized.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    exception<AuthorizationException> { cause ->
        HttpStatusCode.Forbidden.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    exception<UnrecognizedPropertyException> { cause ->
        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, "Unrecognized property '${cause.propertyName}'"))
        }
    }

    exception<MissingKotlinParameterException> { cause ->
        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, "Required field '${cause.parameter.name}' is missing"))
        }
    }

    exception<InvalidDataException> { cause ->
        getLogger().error(cause.stackTraceToString())
        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    exception<DuplicateEntityException> { cause ->
        getLogger().error(cause.stackTraceToString())
        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    exception<BusinessLogicException> { cause ->
        getLogger().error(cause.stackTraceToString())
        HttpStatusCode.InternalServerError.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    exception<UnexpectedException> { cause ->
        getLogger().error(cause.stackTraceToString())
        HttpStatusCode.InternalServerError.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    exception<NoEntityFoundException> { cause ->
        HttpStatusCode.NotFound.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }
}

class AuthenticationException(message: String = "Authentication failure") : Exception(message)
class AuthorizationException(message: String = "Access denied") : Exception(message)
class NoEntityFoundException(message: String = "No such entity was found") : Exception(message)
class DuplicateEntityException(message: String = "Such an entity already exists") : Exception(message)
class InvalidDataException(message: String = "Invalid data") : Exception(message)
class UnexpectedException(message: String = "Unexpected exception occurred") : Exception(message)
class BusinessLogicException(message: String = "Exception in business logic") : Exception(message)

data class ErrorResponse(
    val status: HttpStatusCode,
    val message: String,
    val timestamp: String? = Instant.now().toString()
)