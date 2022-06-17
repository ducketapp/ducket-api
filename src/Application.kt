package io.ducket.api

import clients.rates.ReferenceRatesClient
import io.ducket.api.app.database.AppDatabase
import io.ducket.api.app.di.AppModule
import io.ducket.api.app.scheduler.AppJobFactory
import io.ducket.api.app.scheduler.ObsoleteDataCleanUpJob
import io.ducket.api.auth.UserPrincipal
import io.ducket.api.config.*
import io.ducket.api.domain.repository.CurrencyRepository
import io.ducket.api.domain.service.CurrencyRateService
import io.ducket.api.plugins.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.util.*

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    installDependencyInjection()
    initializeAppConfig()
    initializeDatabases()
    installCallLogging()
    installMetrics()
    installAuthentication()
    installAuthorization()
    installDefaultHeaders()
    installSerialization()
    installRouting()
    installErrorHandling()
    initializeSchedulers()

    // pullReferenceRatesStaticData()
}

private fun Application.pullReferenceRatesStaticData() {
    val referenceRatesClient by inject<ReferenceRatesClient>()
    val currencyRateService by inject<CurrencyRateService>()
    val currencyRepository by inject<CurrencyRepository>()

    CoroutineScope(Job() + Dispatchers.IO).launch {
        val supportedCurrencies = currencyRepository.findAll().map { it.isoCode }
        val result = referenceRatesClient.getAllRates(*supportedCurrencies.toTypedArray())

        currencyRateService.putCurrencyRates(result.dataSet.references)
    }
}

private fun Application.initializeDatabases() {
    val mainDatabase by inject<AppDatabase>(named(AppModule.DatabaseType.MAIN_DB))
    // val schedulerDatabase by inject<AppDatabase>(named(AppModule.DatabaseType.SCHEDULER_DB))

    mainDatabase.connect()
    // schedulerDatabase.connect()

    TransactionManager.defaultDatabase = mainDatabase.database
}

private fun Application.initializeSchedulers() {
    val appConfig by inject<AppConfig>()
    val jobFactory by inject<AppJobFactory>()

    val schedulerProperties = Properties().apply {
        setProperty("org.quartz.scheduler.instanceName", "QuartzScheduler")
        setProperty("org.quartz.threadPool.threadCount", "3")
        setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool")
        setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore")

        setProperty("org.quartz.plugin.jobHistory.class", "org.quartz.plugins.history.LoggingJobHistoryPlugin")
        setProperty("org.quartz.plugin.jobHistory.jobToBeFiredMessage", """Job [{1}.{0}] to be fired by trigger [{4}.{3}], re-fire: {7}""")
        setProperty("org.quartz.plugin.jobHistory.jobSuccessMessage", """Job [{1}.{0}] execution complete and reports: {8}""")
        setProperty("org.quartz.plugin.jobHistory.jobFailedMessage", """Job [{1}.{0}] execution failed with exception: {8}""")
        setProperty("org.quartz.plugin.jobHistory.jobWasVetoedMessage", """Job [{1}.{0}] was vetoed. It was to be fired by trigger [{4}.{3}] at: {2, date, dd-MM-yyyy HH:mm:ss.SSS}""")

        setProperty("org.quartz.plugin.triggerHistory.class", "org.quartz.plugins.history.LoggingTriggerHistoryPlugin")
        setProperty("org.quartz.plugin.triggerHistory.triggerFiredMessage", """Trigger [{1}.{0}] fired job [{6}.{5}] scheduled at: {2, date, dd-MM-yyyy HH:mm:ss.SSS}, next scheduled at: {3, date, dd-MM-yyyy HH:mm:ss.SSS}""")
        setProperty("org.quartz.plugin.triggerHistory.triggerCompleteMessage", """Trigger [{1}.{0}] completed firing job [{6}.{5}] with resulting trigger instruction code: {9}. Next scheduled at: {3, date, dd-MM-yyyy HH:mm:ss.SSS}""")
        setProperty("org.quartz.plugin.triggerHistory.triggerMisfiredMessage", """Trigger [{1}.{0}] misfired job [{6}.{5}]. Should have fired at: {3, date, dd-MM-yyyy HH:mm:ss.SSS}""")

        setProperty("org.quartz.plugin.shutdownHook.class", "org.quartz.plugins.management.ShutdownHookPlugin")
        setProperty("org.quartz.plugin.shutdownHook.cleanShutdown", "true")
    }

    val jobDetail: JobDetail = JobBuilder.newJob(ObsoleteDataCleanUpJob::class.java)
        .withIdentity("ObsoleteDataCleanUpJob", "SchedulerGroup")
        .usingJobData(ObsoleteDataCleanUpJob.JOB_DATA_PATH_KEY, appConfig.localDataConfig.dbDataPath)
        .build()

    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("EveryOneMinuteTrigger", "SchedulerGroup")
        .withSchedule(
            SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMinutes(1)
                .repeatForever()
        ).build()

    val scheduler = StdSchedulerFactory(schedulerProperties).scheduler

    scheduler.start()
    scheduler.setJobFactory(jobFactory)
    scheduler.scheduleJob(jobDetail, trigger)
}

private fun Application.initializeAppConfig() {
    val appConfig by inject<AppConfig>()

    System.setProperty("handlers", "org.slf4j.bridge.SLF4JBridgeHandler")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    val dbDataPath = System.getProperty("data.path", "resources/database/data")
    val ecbDataPath = System.getProperty("ecb.path", Paths.get(System.getProperty("java.io.tmpdir"), "ecb").toString())
    val hoconConfig = environment.config.config("ktor")

    appConfig.apply {
        this.serverConfig = ServerConfig(
            host = hoconConfig.property("deployment.host").getString(),
            port = hoconConfig.property("deployment.port").getString().toInt(),
        )

        this.databaseServerConfig = DatabaseServerConfig(
            schema = DatabaseServerSchemaConfig(
                main = hoconConfig.property("database.schema.main").getString(),
                scheduler = hoconConfig.property("database.schema.scheduler").getString(),
            ),
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

        this.localDataConfig = LocalDataConfig(
            exrDataPath = ecbDataPath,
            dbDataPath = dbDataPath,
        )
    }
}

inline fun <reified T> T.getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

fun AuthenticationContext.principalOrThrow(): UserPrincipal {
    return principal() ?: throw AuthenticationException("Invalid auth token data")
}
