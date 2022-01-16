package io.ducket.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.ConfigFactory
import domain.model.account.AccountsTable
import domain.model.user.UsersTable
import io.ducket.api.app.AppModule
import io.ducket.api.app.database.DatabaseFactory
import io.ducket.api.app.database.TestingDatabaseFactory
import io.ducket.api.config.*
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
import io.ducket.api.utils.TransferCodeGenerator
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
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.module.Module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.SLF4JLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.lang.IllegalArgumentException
import java.net.BindException
import java.util.*

fun main(args: Array<String>): Unit {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(
    testing: Boolean = false,
    diModules: MutableList<Module> = mutableListOf(AppModule.module),
) {
    install(Koin) {
        SLF4JLogger()
        modules(diModules)
    }

    this.setupAppConfig()

    val appConfig by inject<AppConfig>()
    val database by inject<DatabaseFactory>()
    val jwtManager by inject<JwtManager>()
    val ratesClient by inject<CurrencyRatesClient>()

    database.connect()
    ratesClient.pullRates()

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
            realm = appConfig.jwtConfig.realm

            verifier(jwtManager.verifier)
            challenge { _, _ -> throw AuthenticationException("Invalid auth token") }
            validate {
                jwtManager.validateToken(jwtCredential = it)
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

    val userController: UserController by inject()
    val accountController: AccountController by inject()
    val recordController: RecordController by inject()
    val categoryController: CategoryController by inject()
    val budgetController: BudgetController by inject()
    val transactionController: TransactionController by inject()
    val transferController: TransferController by inject()
    val currencyController: CurrencyController by inject()

    install(Routing) {
        route("/api/v1") {
            intercept(ApplicationCallPipeline.Call) {
                if (!call.request.path().contains("auth")) {
                    val currentUserId = call.authentication.principalOrThrow().id

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

            root()
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

@Suppress("unused")
private fun Application.setupAppConfig() {
    val appConfig by inject<AppConfig>()

    System.setProperty("handlers", "org.slf4j.bridge.SLF4JBridgeHandler")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    val dbDataPath = System.getProperty("DB_DATA_PATH", "resources/app")
    val hoconConfig = this.environment.config.config("ktor")

    appConfig.apply {
        this.serverConfig = ServerConfig(
            env = hoconConfig.property("environment").getString(),
            host = hoconConfig.property("deployment.host").getString(),
            port = hoconConfig.property("deployment.port").getString().toInt(),
        )

        this.databaseConfig = DatabaseConfig(
            url = hoconConfig.property("database.url").getString(),
            driver = hoconConfig.property("database.driver").getString(),
            username = hoconConfig.property("database.username").getString(),
            password = hoconConfig.property("database.password").getString(),
            dataPath = dbDataPath,
        )

        this.jwtConfig = JwtConfig(
            secret = hoconConfig.property("jwt.secret").getString(),
            issuer = hoconConfig.property("jwt.issuer").getString(),
            realm = hoconConfig.property("jwt.realm").getString(),
            audience = hoconConfig.property("jwt.audience").getString(),
        )
    }
}

inline fun <reified T> T.getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

fun AuthenticationContext.principalOrThrow(): UserPrincipal {
    return principal() ?: throw AuthenticationException("Invalid auth token data")
}
