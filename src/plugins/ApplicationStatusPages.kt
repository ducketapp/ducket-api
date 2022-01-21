package io.ducket.api.plugins

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ducket.api.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import java.time.Instant
import java.util.*


fun StatusPages.Configuration.defaultStatusPages() {
    status(HttpStatusCode.NotFound) { cause ->
        call.respond(cause, ErrorResponse(cause, "Route not found"))
    }

    status(HttpStatusCode.Unauthorized) { cause ->
        call.respond(cause, ErrorResponse(cause, cause.description))
    }
}

fun StatusPages.Configuration.applicationStatusPages() {
    val logger = getLogger()

    /**
     * HttpStatusCode.PreconditionFailed
     */
    exception<ConstraintViolationException> { cause ->
        HttpStatusCode.PreconditionFailed.apply {
            val errors = cause.constraintViolations
                .mapToMessage("messages", Locale.ENGLISH)
                .map { "The '${it.property}' field ${it.message.toLowerCase()}" }

            call.respond(this, ErrorResponse(this, errors[0]))
        }
    }

    /**
     * HttpStatusCode.BadRequest
     */
    exception<UnrecognizedPropertyException> { cause ->
        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, "Unrecognized property '${cause.propertyName}'"))
        }
    }

    /**
     * HttpStatusCode.BadRequest
     */
    exception<MissingKotlinParameterException> { cause ->
        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, "Required field '${cause.parameter.name}' is missing"))
        }
    }

    /**
     * HttpStatusCode.BadRequest
     */
    exception<InvalidDataException> { cause ->
        logger.error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    /**
     * HttpStatusCode.BadRequest
     */
    exception<DuplicateEntityException> { cause ->
        logger.error(cause.stackTraceToString())

        HttpStatusCode.BadRequest.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    /**
     * HttpStatusCode.InternalServerError
     */
    exception<Throwable> { cause ->
        logger.error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.apply {
            call.respond(this, ErrorResponse(this, "Oops, something went wrong!"))
        }
    }

    /**
     * HttpStatusCode.InternalServerError
     */
    exception<BusinessException> { cause ->
        logger.error(cause.stackTraceToString())

        HttpStatusCode.InternalServerError.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    /**
     * HttpStatusCode.Unauthorized
     */
    exception<AuthenticationException> { cause ->
        HttpStatusCode.Unauthorized.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    /**
     * HttpStatusCode.Forbidden
     */
    exception<AuthorizationException> { cause ->
        HttpStatusCode.Forbidden.apply {
            call.respond(this, ErrorResponse(this, cause.localizedMessage))
        }
    }

    /**
     * HttpStatusCode.NotFound
     */
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
class BusinessException(message: String = "Exception in business logic") : Exception(message)

data class ErrorResponse(
    val status: HttpStatusCode,
    val message: String,
    val timestamp: String? = Instant.now().toString()
)