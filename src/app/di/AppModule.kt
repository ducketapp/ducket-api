package io.ducket.api.app.di

import clients.rates.ReferenceRatesClient
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ducket.api.app.database.*
import io.ducket.api.app.scheduler.AppJobFactory
import io.ducket.api.config.AppConfig
import io.ducket.api.auth.JwtManager
import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.currency.CurrencyController
import io.ducket.api.domain.controller.group.GroupController
import io.ducket.api.domain.controller.operation.OperationController
import io.ducket.api.domain.controller.rule.ImportRuleController
import io.ducket.api.domain.controller.tag.TagController
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.domain.repository.*
import io.ducket.api.domain.service.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object AppModule {
    enum class DatabaseType {
        MAIN_DB, SCHEDULER_DB
    }

    val configurationModule = module {
        single { AppConfig() }
    }

    val authenticationModule = module {
        single { JwtManager(get()) }
    }

    val databaseModule = module {
        single<AppDatabase>(named(DatabaseType.MAIN_DB)) { MainDatabase(get()) }
    }

    val schedulerModule = module {
        single { AppJobFactory(get(), get(), get()) }
    }

    val controllerModule = module {
        single { UserController(get()) }
        single { AccountController(get(), get()) }
        single { CategoryController(get()) }
        single { BudgetController(get()) }
        single { CurrencyController(get()) }
        single { ImportRuleController(get()) }
        single { OperationController() }
        single { GroupController(get()) }
        single { TagController(get()) }
    }

    val serviceModule = module {
        single { UserService(get(), get()) }
        single { AccountService(get()) }
        single { CategoryService(get()) }
        single { BudgetService(get(), get(), get(), get()) }
        single { CurrencyService(get(), get()) }
        single { ImportRuleService(get()) }
        single { ImportService(get(), get(), get(), get(), get()) }
        single { LocalFileService() }
        single { OperationService(get(), get(), get(), get(), get(), get()) }
        single { GroupService(get(), get(), get(), get(), get()) }
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
        single { BudgetPeriodLimitRepository() }
        single { OperationRepository() }
        single { OperationAttachmentRepository() }
        single { GroupRepository() }
        single { GroupMembershipRepository() }
        single { GroupMemberAccountPermissionRepository() }
        single { AttachmentRepository() }
        single { TagRepository() }
    }

    val clientModule = module {
        single<ReferenceRatesClient> { ReferenceRatesClient(get()) }
        single<HttpClient> {
            HttpClient(Apache) {
                val baseUrl = "https://sdw-wsrest.ecb.europa.eu/service/data/EXR"

                defaultRequest {
                    url.takeFrom(URLBuilder().takeFrom(baseUrl).apply {
                        encodedPath += url.encodedPath
                    })
                }

                install(JsonFeature) {
                    serializer = JacksonSerializer(jackson = XmlMapper().registerModule(KotlinModule.Builder().build()))
                    accept(ContentType("application", "vnd.sdmx.structurespecificdata+xml"))
                }

                install(Logging) {
                    level = LogLevel.INFO
                    logger = Logger.DEFAULT
                }

                engine {
                    socketTimeout = TimeUnit.SECONDS.toMillis(25).toInt()
                    connectTimeout = TimeUnit.SECONDS.toMillis(25).toInt()
                    connectionRequestTimeout = TimeUnit.SECONDS.toMillis(25).toInt()
                }

                expectSuccess = true
            }
        }
    }
}