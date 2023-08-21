package org.expenny.service.app.di

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.expenny.service.app.database.*
import org.expenny.service.app.scheduler.AppJobFactory
import org.expenny.service.config.AppConfig
import org.expenny.service.auth.authentication.JwtManager
import org.expenny.service.clients.rates.ReferenceRatesClient
import org.expenny.service.domain.controller.account.AccountController
import org.expenny.service.domain.controller.periodic_budget.PeriodicBudgetController
import org.expenny.service.domain.controller.budget.BudgetController
import org.expenny.service.domain.controller.category.CategoryController
import org.expenny.service.domain.controller.currency.CurrencyController
import org.expenny.service.domain.controller.imports.ImportController
import org.expenny.service.domain.controller.operation.OperationController
import org.expenny.service.domain.controller.rule.ImportRuleController
import org.expenny.service.domain.controller.tag.TagController
import org.expenny.service.domain.controller.user.UserController
import org.expenny.service.domain.repository.*
import org.expenny.service.domain.service.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.jackson.*
import io.ktor.client.plugins.json.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object AppModule {

    val configurationModule = module {
        single { AppConfig() }
    }

    val authenticationModule = module {
        single { JwtManager(get()) }
    }

    val databaseModule = module {
        single<AppDatabase> { MainDatabase(get()) }
    }

    val schedulerModule = module {
        single { AppJobFactory(get(), get()) }
    }

    val controllerModule = module {
        single { UserController(get()) }
        single { AccountController(get()) }
        single { CategoryController(get()) }
        single { BudgetController(get()) }
        single { PeriodicBudgetController(get(), get()) }
        single { CurrencyController(get()) }
        single { ImportController(get()) }
        single { ImportRuleController(get()) }
        single { OperationController(get()) }
        single { TagController(get()) }
    }

    val serviceModule = module {
        single { UserService(get(), get()) }
        single { AccountService(get()) }
        single { CategoryService(get()) }
        single { BudgetService(get(), get()) }
        single { PeriodicBudgetService(get(), get(), get()) }
        single { PeriodicBudgetLimitService(get(), get()) }
        single { CurrencyService(get(), get()) }
        single { ImportRuleService(get()) }
        single { ImportService(get(), get(), get(), get()) }
        single { OperationService(get(), get(), get()) }
        single { TagService(get()) }
    }

    val repositoryModule = module {
        single { UserRepository() }
        single { CategoryRepository() }
        single { CurrencyRepository() }
        single { CurrencyRateRepository() }
        single { ImportRepository() }
        single { ImportRuleRepository() }
        single { AccountRepository() }
        single { BudgetRepository() }
        single { BudgetAccountRepository() }
        single { PeriodicBudgetRepository() }
        single { PeriodicBudgetLimitRepository() }
        single { PeriodicBudgetAccountRepository() }
        single { OperationRepository() }
        single { TagRepository() }
    }

    val clientModule = module {
        single<ReferenceRatesClient> { ReferenceRatesClient(get()) }
        single<HttpClient> {
            HttpClient(Apache) {
                val baseUrl = "https://sdw-wsrest.ecb.europa.eu/service/data/EXR/"

                defaultRequest {
                    url.takeFrom(URLBuilder()
                        .takeFrom(baseUrl)
                        .apply { encodedPath += url.encodedPath }
                    )
                }

                install(JsonPlugin) {
                    serializer = JacksonSerializer(jackson = XmlMapper().registerModule(KotlinModule.Builder().build()))
                    accept(ContentType("application", "vnd.sdmx.structurespecificdata+xml"))
                }

                install(Logging) {
                    level = LogLevel.INFO
                    logger = Logger.DEFAULT
                }

                engine {
                    socketTimeout = TimeUnit.SECONDS.toMillis(30).toInt()
                    connectTimeout = TimeUnit.SECONDS.toMillis(30).toInt()
                    connectionRequestTimeout = TimeUnit.SECONDS.toMillis(30).toInt()
                }

                expectSuccess = true
            }
        }
    }
}