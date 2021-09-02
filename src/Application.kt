package io.budgery.api

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.budgery.api.config.DatabaseConfig
import io.budgery.api.config.DependencyInjectionConfig
import io.budgery.api.config.JwtConfig
import io.budgery.api.config.UserPrincipal
import io.budgery.api.domain.controller.account.AccountController
import io.budgery.api.domain.controller.budget.BudgetController
import io.budgery.api.domain.controller.category.CategoryController
import io.budgery.api.domain.controller.label.LabelController
import io.budgery.api.domain.controller.record.RecordController
import io.budgery.api.domain.controller.user.UserController
import io.budgery.api.domain.repository.*
import io.budgery.api.domain.service.*
import io.budgery.api.route.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.jackson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.kodein.di.generic.instance
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import java.lang.IllegalArgumentException
import java.time.Instant
import java.util.*
import kotlin.NoSuchElementException

const val PORT = 8080;

@EngineAPI
fun main() {
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
        watchPaths = listOf("classes")
    )
}

fun Application.module() {
    val userController by DependencyInjectionConfig.kodein.instance<UserController>()
    val accountController by DependencyInjectionConfig.kodein.instance<AccountController>()
    val recordController by DependencyInjectionConfig.kodein.instance<RecordController>()
    val labelController by DependencyInjectionConfig.kodein.instance<LabelController>()
    val categoryController by DependencyInjectionConfig.kodein.instance<CategoryController>()
    val budgetController by DependencyInjectionConfig.kodein.instance<BudgetController>()

    val userRepository = UserRepository()

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
        jwt {
            realm = "io.budgery.api"
            verifier(JwtConfig.verifier)
            challenge { scheme, realm -> throw AuthenticationException("Invalid auth token") }
            validate {
                val jwtUserId = it.payload.getClaim("id").asInt()
                val jwtUserUuid = it.payload.getClaim("uuid").asString()
                val jwtUserEmail = it.payload.getClaim("email").asString()
                val user = userRepository.findById(jwtUserId)

                if (user == null) throw Exception("User not found")
                else UserPrincipal(jwtUserId, UUID.fromString(jwtUserUuid), jwtUserEmail)
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
            call.respond(cause, ErrorResponse(cause, "Not found"))
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
        transactions(recordController)
        labels(labelController)
        budgets(budgetController)
    }
}

class AuthenticationException(message: String = "Authentication failure") : Exception(message)
class AuthorizationException(message: String = "Access denied") : Exception(message)
data class ErrorResponse(val status: HttpStatusCode, val message: String, val timestamp: String? = Instant.now().toString())

