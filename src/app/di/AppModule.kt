package io.ducket.api.app.di

import io.ducket.api.CurrencyRateProvider
import io.ducket.api.app.database.AppDatabaseFactory
import io.ducket.api.app.database.DatabaseFactory
import io.ducket.api.app.database.TestingDatabaseFactory
import io.ducket.api.config.AppConfig
import io.ducket.api.config.JwtManager
import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.currency.CurrencyController
import io.ducket.api.domain.controller.group.GroupController
import io.ducket.api.domain.controller.ledger.LedgerController
import io.ducket.api.domain.controller.rule.ImportRuleController
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.domain.repository.*
import io.ducket.api.domain.service.*
import org.koin.dsl.module
import org.koin.dsl.single

object AppModule {

    internal val testConfigModule = module {
        /**
         * Configuration
         */
        single<AppConfig>()
        single<DatabaseFactory> { TestingDatabaseFactory() }
        single { JwtManager(get()) }
    }

    internal val configModule = module {
        /**
         * Configuration
         */
        single<AppConfig>()
        single<DatabaseFactory> { AppDatabaseFactory(get()) }
        single { JwtManager(get()) }
    }

    internal val appModule = module {
        /**
         * Clients
         */
        single<CurrencyRateProvider>()

        /**
         * Controllers
         */
        single { UserController(get()) }
        single { AccountController(get(), get()) }
        single { CategoryController(get()) }
        single { BudgetController(get()) }
        single { CurrencyController(get()) }
        single { GroupController(get()) }
        single { ImportRuleController(get()) }
        single { LedgerController(get()) }

        /**
         * Services
         */
        single { UserService(get(), get()) }
        single { AccountService(get(), get(), get(), get()) }
        single { CategoryService(get()) }
        single { BudgetService(get(), get(), get()) }
        single { CurrencyService(get()) }
        single { ImportRuleService(get()) }
        single { ImportService(get(), get(), get(), get(), get(), get()) }
        single { GroupService(get(), get(), get()) }
        single { LocalFileService() }
        single { LedgerService(get(), get(), get(), get(), get(), get()) }

        /**
         * Repositories
         */
        single { UserRepository() }
        single { CategoryRepository() }
        single { CurrencyRepository() }
        single { ImportRepository() }
        single { ImportRuleRepository() }
        single { AccountRepository() }
        single { BudgetRepository() }
        single { GroupRepository() }
        single { GroupMembershipRepository() }
        single { OperationRepository() }
        single { OperationAttachmentRepository() }
        single { LedgerRepository() }
    }
}