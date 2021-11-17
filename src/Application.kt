package io.ducket.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ducket.api.config.DatabaseConfig
import io.ducket.api.config.DependencyInjectionConfig
import io.ducket.api.config.JwtConfig
import io.ducket.api.config.UserPrincipal
import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.label.LabelController
import io.ducket.api.domain.controller.record.RecordController
import io.ducket.api.domain.controller.transaction.TransactionController
import io.ducket.api.domain.controller.transfer.TransferController
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.domain.repository.UserRepository
import io.ducket.api.route.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.kodein.di.generic.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import java.time.Instant
import java.util.*

const val PORT = 8080

@EngineAPI
fun main() {
    // System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
    System.setProperty("handlers", "org.slf4j.bridge.SLF4JBridgeHandler")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    // ECB
    ExchangeRateClient().pullRates()

    setup().start(wait = true)
}

@EngineAPI
fun setup(): BaseApplicationEngine {
    DatabaseConfig.setup(
        "jdbc:mysql://127.0.0.1:3306/budgery?useUnicode=true&serverTimezone=UTC&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
        "root",
        "toor",
    )

    return embeddedServer(
        factory = Netty,
        port = PORT,
        module = Application::module,
        watchPaths = listOf("classes"),
    )
}

fun Application.module() {
    val userController by DependencyInjectionConfig.kodein.instance<UserController>()
    val accountController by DependencyInjectionConfig.kodein.instance<AccountController>()
    val recordController by DependencyInjectionConfig.kodein.instance<RecordController>()
    val labelController by DependencyInjectionConfig.kodein.instance<LabelController>()
    val categoryController by DependencyInjectionConfig.kodein.instance<CategoryController>()
    val budgetController by DependencyInjectionConfig.kodein.instance<BudgetController>()
    val transactionController by DependencyInjectionConfig.kodein.instance<TransactionController>()
    val transferController by DependencyInjectionConfig.kodein.instance<TransferController>()

    val userRepository = UserRepository()

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val path = call.request.path()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "($status) [$httpMethod], $userAgent - $path"
        }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
        jwt {
            realm = "io.ducket.api"
            verifier(JwtConfig.verifier)
            challenge { _, _ -> throw AuthenticationException("Invalid auth token") }
            validate {
                val jwtUserId = it.payload.getClaim("id").asString()
                val jwtUserEmail = it.payload.getClaim("email").asString()

                return@validate userRepository.findOne(jwtUserId)?.let {
                    UserPrincipal(jwtUserId, jwtUserEmail)
                } ?: throw AuthenticationException("User not found")
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(StatusPages) {
        status(HttpStatusCode.NotFound) { cause ->
            call.respond(cause, ErrorResponse(cause, "Route not found"))
        }

        status(HttpStatusCode.Unauthorized) { cause ->
            call.respond(cause, ErrorResponse(cause, cause.description))
        }

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

        exception<MissingKotlinParameterException> { cause ->
            HttpStatusCode.BadRequest.apply {
                call.respond(this, ErrorResponse(this, "Required field '${cause.parameter.name}' is missing"))
            }
        }

        exception<IllegalArgumentException> { cause ->
            log.error(cause.stackTraceToString())

            HttpStatusCode.BadRequest.apply {
                call.respond(this, ErrorResponse(this, cause.localizedMessage))
            }
        }

        exception<DuplicateEntityError> { cause ->
            log.error(cause.stackTraceToString())

            HttpStatusCode.BadRequest.apply {
                call.respond(this, ErrorResponse(this, cause.localizedMessage))
            }
        }

        /**
         * HttpStatusCode.InternalServerError
         */
        exception<Throwable> { cause ->
            log.error(cause.stackTraceToString())
            HttpStatusCode.InternalServerError.apply {
                call.respond(this, ErrorResponse(this, "Oops, something went wrong!"))
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
        exception<NoSuchElementException> { cause ->
            HttpStatusCode.NotFound.apply {
                call.respond(this, ErrorResponse(this, cause.localizedMessage))
            }
        }
    }

    install(Routing) {
        users(userController)
        accounts(accountController)
        categories(categoryController)
        records(recordController)
        transfers(transferController)
        transactions(transactionController)
        labels(labelController)
        budgets(budgetController)
    }
}

inline fun <reified T> T.getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

class AuthenticationException(message: String = "Authentication failure") : Exception(message)
class AuthorizationException(message: String = "Access denied") : Exception(message)
class NoEntityFoundError(message: String = "No such entity was found") : Exception(message)
class DuplicateEntityError(message: String = "Such an entity already exists") : Exception(message)
class InvalidDataError(message: String = "Inappropriate data") : Exception(message)
data class ErrorResponse(
    val status: HttpStatusCode,
    val message: String,
    val timestamp: String? = Instant.now().toString()
)

