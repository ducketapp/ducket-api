package io.ducket.api.app

import io.ducket.api.CurrencyRateProvider
import io.ducket.api.app.database.AppDatabaseFactory
import io.ducket.api.app.database.DatabaseFactory
import io.ducket.api.config.AppConfig
import io.ducket.api.config.JwtManager
import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.currency.CurrencyController
import io.ducket.api.domain.controller.record.RecordController
import io.ducket.api.domain.controller.transaction.TransactionController
import io.ducket.api.domain.controller.transfer.TransferController
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.domain.repository.*
import io.ducket.api.domain.service.*
import org.koin.dsl.module
import org.koin.dsl.single

object AppModule {

    internal val module = module {
        /**
         * Application
         */
        single<AppConfig>()
        single<CurrencyRateProvider>()
        single<DatabaseFactory> { AppDatabaseFactory(get()) }
        single { JwtManager(get()) }

        /**
         * Controllers
         */
        single { UserController(get()) }
        single { AccountController(get(), get()) }
        single { CategoryController(get()) }
        single { RecordController(get(), get(), get()) }
        single { TransactionController(get(), get()) }
        single { TransferController(get(), get()) }
        single { BudgetController(get()) }
        single { CurrencyController(get()) }

        /**
         * Services
         */
        single { UserService(get(), get(), get()) }
        single { AccountService(get(), get(), get(), get()) }
        single { CategoryService(get()) }
        single { TransferService(get(), get(), get()) }
        single { TransactionService(get(), get()) }
        single { BudgetService(get(), get(), get(), get(), get(), get()) }
        single { CurrencyService(get()) }
        single { ImportRuleService(get()) }
        single { ImportService(get(), get(), get(), get()) }

        /**
         * Repositories
         */
        single { UserRepository() }
        single { CategoryRepository() }
        single { CurrencyRepository() }
        single { FollowRepository() }
        single { ImportRepository() }
        single { ImportRuleRepository() }
        single { AccountRepository(get()) }
        single { BudgetRepository(get()) }
        single { TransactionRepository(get()) }
        single { TransferRepository(get()) }
    }
}