package io.ducket.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.ConfigFactory
import io.ducket.api.config.DatabaseConfig
import io.ducket.api.config.KodeinConfig
import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.currency.CurrencyController
import io.ducket.api.domain.controller.record.RecordController
import io.ducket.api.domain.controller.transaction.TransactionController
import io.ducket.api.domain.controller.transfer.TransferController
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.plugins.AuthenticationException
import io.ducket.api.plugins.AuthorizationException
import io.ducket.api.plugins.applicationStatusPages
import io.ducket.api.plugins.defaultStatusPages
import io.ducket.api.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.kodein.di.generic.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.*

inline fun <reified T> T.getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

@EngineAPI
fun main() {
    System.setProperty("handlers", "org.slf4j.bridge.SLF4JBridgeHandler")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    val env = System.getenv()["ENVIRONMENT"] ?: "dev"
    val appConfig = HoconApplicationConfig(ConfigFactory.load()).config("ktor.deployment.${env}")

    ExchangeRateClient.pullRates()
    DatabaseConfig.init(appConfig)

    embeddedServer(
        factory = Netty,
        configure = {
            connectionGroupSize = 2
            workerGroupSize = 5
            callGroupSize = 10
        },
        environment = applicationEngineEnvironment {
            log = LoggerFactory.getLogger("io.ducket.api")
            developmentMode = env == "dev"
            config = appConfig

            module {
                this.module()
            }

            connector {
                port = appConfig.property("port").getString().toInt()
                host = appConfig.property("host").getString()
            }
        }
    ).start(true)
}

fun Application.module() {
    val userController by KodeinConfig.kodein.instance<UserController>()
    val accountController by KodeinConfig.kodein.instance<AccountController>()
    val recordController by KodeinConfig.kodein.instance<RecordController>()
    val categoryController by KodeinConfig.kodein.instance<CategoryController>()
    val budgetController by KodeinConfig.kodein.instance<BudgetController>()
    val transactionController by KodeinConfig.kodein.instance<TransactionController>()
    val transferController by KodeinConfig.kodein.instance<TransferController>()
    val currencyController by KodeinConfig.kodein.instance<CurrencyController>()

    install(CallLogging) {
        level = Level.DEBUG

        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val uri = call.request.uri
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "($status) [$httpMethod], $userAgent - $uri"
        }
    }

    install(Authentication) {
        jwt {
            realm = JwtConfig.realm

            verifier(JwtConfig.verifier)
            challenge { _, _ -> throw AuthenticationException("Invalid auth token") }
            validate {
                JwtConfig.validateToken(jwtCredential = it)
            }
        }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Routing) {
        route("/api/v1") {
            intercept(ApplicationCallPipeline.Call) {
                if (!call.request.path().contains("auth")) {
                    val currentUserId = JwtConfig.getPrincipal(call.authentication).id

                    call.parameters["userId"]?.toLong()?.let { requestedUserId ->
                        if (currentUserId != requestedUserId) {
                            // let the user to access user data in readonly mode
                            if (call.request.httpMethod != HttpMethod.Get) {
                                throw AuthorizationException("Access restricted")
                            }
                        }
                    }
                }
            }

            users(userController)
            accounts(accountController)
            categories(categoryController)
            records(recordController, transactionController, transferController, userController)
            budgets(budgetController)
            currencies(currencyController)
        }
    }

    install(StatusPages) {
        defaultStatusPages()
        applicationStatusPages()
    }
}
