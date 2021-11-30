package io.ducket.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.ConfigFactory
import io.ducket.api.config.DatabaseConfig
import io.ducket.api.config.KodeinConfig
import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.label.LabelController
import io.ducket.api.domain.controller.record.RecordController
import io.ducket.api.domain.controller.transaction.TransactionController
import io.ducket.api.domain.controller.transfer.TransferController
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.plugins.AuthenticationException
import io.ducket.api.plugins.applicationStatusPages
import io.ducket.api.plugins.defaultStatusPages
import io.ducket.api.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
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
    val labelController by KodeinConfig.kodein.instance<LabelController>()
    val categoryController by KodeinConfig.kodein.instance<CategoryController>()
    val budgetController by KodeinConfig.kodein.instance<BudgetController>()
    val transactionController by KodeinConfig.kodein.instance<TransactionController>()
    val transferController by KodeinConfig.kodein.instance<TransferController>()

    install(CallLogging) {
        level = Level.DEBUG

        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val path = call.request.path()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "($status) [$httpMethod], $userAgent - $path"
        }
    }

    install(Authentication) {
        jwt {
            realm = JwtConfig.realm

            verifier(JwtConfig.verifier)
            challenge { _, _ -> throw AuthenticationException("Invalid auth token") }
            validate { JwtConfig.validateToken(it) }
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

    install(StatusPages) {
        defaultStatusPages()
        applicationStatusPages()
    }
}

/*fun main() {
    val trainingFile = File("resources/labeled_transactions.csv")
    val trainingFileReader = BufferedReader(InputStreamReader(trainingFile.inputStream()))
    val trainingFileParser = CSVParser(
        trainingFileReader, CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(',')
            .withIgnoreHeaderCase().withTrim()
    )

    var data = trainingFileParser.records.map {
        try {
            val category = it[0]
            val text = it[1].trimWhitespaces()

            return@map category to text
        } catch (e: Exception) {
            throw InvalidDataError("Invalid value, row #${it.recordNumber}: ${e.message}\n${it.toMap()}")
        }
    }

    val nbc = data.toNaiveBayesClassifier(
        featuresSelector = { pair ->
            pair.second.split(Regex("\\s")).asSequence()
                .map { it.replace(Regex("[^A-Za-z]"), "").toLowerCase() }
                .filter { it.isNotEmpty() }.distinct().toSet()
        },
        categorySelector = { it.first!! }
    )

    val millenniumFile = File("resources/Account_activity_20210923_200103.csv")
    val millenniumFileReader = BufferedReader(InputStreamReader(millenniumFile.inputStream()))
    val millenniumFileParser = CSVParser(
        millenniumFileReader, CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(',')
            .withIgnoreHeaderCase().withTrim()
    )

    var memos = millenniumFileParser.records.map {
        try {
            val payee = it[6].trimWhitespaces()
            val notes = it[3].trimWhitespaces()

            return@map "$payee $notes"
        } catch (e: Exception) {
            throw InvalidDataError("Invalid value, row #${it.recordNumber}: ${e.message}\n${it.toMap()}")
        }
    }

    memos.forEach {
        val s = it.split(Regex("\\s")).asSequence()
            .map { it.replace(Regex("[^A-Za-z]"), "").toLowerCase() }
            .filter { it.isNotEmpty() }.distinct().toSet()

        val result = nbc.predict(s)

        println("${result.toString()}\t${it}")
    }
}*/

/*fun main() {
    val millenniumExportFile = File("resources/Account_activity_20210923_200103.csv")

    val reader = BufferedReader(InputStreamReader(millenniumExportFile.inputStream()))

    val millenniumCsvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withDelimiter(',')
        .withIgnoreHeaderCase().withTrim())

    var csvTransactions = millenniumCsvParser.records.map {
        try {
            val date = LocalDate.parse(it[1]).atStartOfDay().toInstant(ZoneOffset.UTC)
            val beneficiaryOrSender = it[5].trimWhitespaces()
            val category = ""
            val notes = it[6].trimWhitespaces()
            val amount = it[7].toBigDecimalOrNull() ?: it[8].toBigDecimal()

            return@map CsvTransaction(date, category, beneficiaryOrSender, notes, amount)
        } catch (e: Exception) {
            throw InvalidDataError("Invalid value, row #${it.recordNumber}: ${e.message}\n${it.toMap()}")
        }
    }

    val memo = csvTransactions.map { e ->
        val text = "${e.beneficiaryOrSender} ${e.notes}"
        text.split(Regex("\\s")).asSequence()
            .map { it.replace(Regex("[^A-Za-z]"), "").toLowerCase() }
            .filter { it.isNotEmpty() }.distinct().joinToString(" ")
    }
    val distinctMemo = memo.toMutableList()
    val start = Instant.now()

    for (i in memo.indices) {
        for (j in i + 1 until memo.size) {
            val e1 = memo[i]
            val e2 = memo[j]

            val cosineDistance = CosineDistance().apply(e1, e2)
            val cosineSimilarityPercentage = ((1 - cosineDistance) * 100).roundToInt().toDouble()

            if (cosineSimilarityPercentage >= 70) {
                distinctMemo.remove(e2)
            }
        }
    }

    *//*memo.forEachIndexed { i1, e1 ->
        for (i2 in i1 + 1 until memo.size) {
            *//**//*val e2 = distinct[i2].payee

            if (e2.isNotBlank() && e1.isNotBlank()) {
                val distance = LevenshteinDistance.getDefaultInstance().apply(e2.toLowerCase(), e1.toLowerCase())
                if (distance in 1..10) {
                    res.remove(distinct[i2])
                }
            }*//**//*

            val input2 = memo[i2]
            val input1 = e1

            if (input1.isNotBlank() && input2.isNotBlank()) {
                val cosineDistance = CosineDistance().apply(input1, input2)
                val cosineSimilarityPercentage = Math.round((1 - cosineDistance) * 100).toDouble()

                if (cosineSimilarityPercentage >= 75) {
                    distinctMemo.remove(memo[i2])
                }
            }
        }
    }*//*

    val end = Instant.now()
    distinctMemo.forEach {
        println(it)
    }

    val m = distinctMemo.joinToString("\n")

    println(csvTransactions.size)
    println(distinctMemo.size)
    println(Duration.between(start, end).toMillis())

    println("All set!")
}*/

//@EngineAPI
//fun main() {
//    // System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
//    System.setProperty("handlers", "org.slf4j.bridge.SLF4JBridgeHandler")
//    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
//
//    // ECB
//    ExchangeRateClient().pullRates()
//
//    setup().start(wait = true)
//}

//fun main(args: Array<String>) {
//    System.setProperty("handlers", "org.slf4j.bridge.SLF4JBridgeHandler")
//    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
//
//    val env = System.getenv()["ENVIRONMENT"] ?: "dev"
//    val appConfig = HoconApplicationConfig(ConfigFactory.load()).config("ktor.deployment.$env")
//
//    DatabaseConfig.init(appConfig)
//
//    ExchangeRateClient().pullRates()
//
////    DatabaseConfig.setup(
////        "jdbc:mysql://127.0.0.1:3306/budgery?useUnicode=true&serverTimezone=UTC&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
////        "root",
////        "toor",
////    )
//
//    EngineMain.main(args)
//}

