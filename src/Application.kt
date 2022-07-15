package io.ducket.api

import io.ducket.api.app.database.AppDatabase
import io.ducket.api.app.scheduler.AppCurrencyRatesPullJob
import io.ducket.api.app.scheduler.AppJobFactory
import io.ducket.api.auth.UserPrincipal
import io.ducket.api.clients.rates.ReferenceRatesClient
import io.ducket.api.config.*
import io.ducket.api.domain.service.CurrencyService
import io.ducket.api.plugins.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.koin.ktor.ext.inject
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

@Suppress("unused")
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    installDependencyInjection()
    setupAppConfig()
    setupDatabases()
    installCallLogging()
    installMetrics()
    installAuthentication()
    installAuthorization()
    installDefaultHeaders()
    installSerialization()
    installRouting()
    installErrorHandling()
    setupScheduler()
    pullReferenceRatesStaticData()
}

private fun Application.pullReferenceRatesStaticData() {
    val appConfig by inject<AppConfig>()

    if (appConfig.dataConfig.pullRates) {
        val referenceRatesClient by inject<ReferenceRatesClient>()
        val currencyRateService by inject<CurrencyService>()
        val currencyService by inject<CurrencyService>()

        runBlocking {
            log.info("Starting to cache static exchange rates from external client...")

            measureTimeMillis {
                val currencies = currencyService.getCurrencies().map { it.isoCode }
                val result = referenceRatesClient.getAll(*currencies.toTypedArray())

                currencyRateService.deleteAllCurrencyRates()
                currencyRateService.putCurrencyRates(result.dataSet.references, result.header.sender.id)
            }.also {
                log.info("Operation has been completed in $it ms.")
            }
        }
    }
}

private fun Application.setupDatabases() {
    val mainDatabase by inject<AppDatabase>()

    mainDatabase.connect()
    TransactionManager.defaultDatabase = mainDatabase.database
}

private fun Application.setupScheduler() {
    val jobFactory by inject<AppJobFactory>()

    val currencyRatesPullJob = JobBuilder.newJob(AppCurrencyRatesPullJob::class.java)
        .withIdentity("CurrencyRatesPullJob", "RegularGroup")
        .build()

    val everyWeekdayAfternoonTrigger = TriggerBuilder.newTrigger()
        .withIdentity("EveryWeekdayAfternoonTrigger", "RegularGroup")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 15 ? * MON-FRI *").inTimeZone(TimeZone.getDefault()))
        .build()

    with(StdSchedulerFactory().scheduler) {
        start()
        setJobFactory(jobFactory)
        scheduleJob(currencyRatesPullJob, everyWeekdayAfternoonTrigger)
    }
}

private fun Application.setupAppConfig() {
    val appConfig by inject<AppConfig>()

    System.setProperty("handlers", "org.slf4j.bridge.SLF4JBridgeHandler")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    val dbPullRates = System.getProperty("db.pullRates", "true")
    val hoconConfig = environment.config.config("ktor")

    appConfig.apply {
        this.serverConfig = ServerConfig(
            host = hoconConfig.property("deployment.host").getString(),
            port = hoconConfig.property("deployment.port").getString().toInt(),
        )

        this.databaseServerConfig = DatabaseServerConfig(
            name = hoconConfig.property("database.name").getString(),
            driver = hoconConfig.property("database.driver").getString(),
            host = hoconConfig.property("database.host").getString(),
            port = hoconConfig.property("database.port").getString().toInt(),
            user = hoconConfig.property("database.user").getString(),
            password = hoconConfig.property("database.password").getString(),
        )

        this.jwtConfig = JwtConfig(
            secret = hoconConfig.property("jwt.secret").getString(),
            issuer = "${serverConfig.host}:${serverConfig.port}",
            audience = hoconConfig.property("jwt.audience").getString(),
        )

        this.dataConfig = DataConfig(
            pullRates = dbPullRates.toBoolean(),
        )
    }
}

inline fun <reified T> T.getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

fun AuthenticationContext.principalOrThrow(): UserPrincipal {
    return principal() ?: throw AuthenticationException("Invalid auth token data")
}
