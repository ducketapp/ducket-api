package io.ducket.api.config

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
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

object KodeinConfig {

    private val userModule = Kodein.Module(name = "userModule") {
        bind() from singleton { UserController(instance(), instance(), instance()) }
        bind() from singleton { UserService(instance(), instance(), instance()) }
        bind() from singleton { UserRepository() }
        bind() from singleton { FollowRepository() }
    }

    private val accountModule = Kodein.Module(name = "accountModule") {
        bind() from singleton { AccountController(instance(), instance()) }
        bind() from singleton { AccountService(instance(), instance(), instance(), instance()) }
        bind() from singleton { AccountRepository(instance()) }
    }

    private val recordModule = Kodein.Module(name = "recordModule") {
        bind() from singleton { RecordController(instance(), instance(), instance()) }
        bind() from singleton { TransactionController(instance(), instance()) }
        bind() from singleton { TransferController(instance(), instance()) }
        bind() from singleton { TransactionService(instance(), instance()) }
        bind() from singleton { TransferService(instance(), instance(), instance()) }
        bind() from singleton { TransactionRepository(instance()) }
        bind() from singleton { TransferRepository(instance()) }
    }

    private val categoryModule = Kodein.Module(name = "categoryModule") {
        bind() from singleton { CategoryController(instance()) }
        bind() from singleton { CategoryService(instance()) }
        bind() from singleton { CategoryRepository() }
    }

    private val budgetModule = Kodein.Module(name = "budgetModule") {
        bind() from singleton { BudgetController(instance()) }
        bind() from singleton { BudgetService(instance(), instance(), instance(), instance(), instance(), instance()) }
        bind() from singleton { BudgetRepository(instance()) }
    }

    private val importModule = Kodein.Module(name = "importModule") {
        bind() from singleton { ImportService(instance(), instance(), instance(), instance()) }
        bind() from singleton { ImportRepository() }
        bind() from singleton { ImportRuleRepository() }
    }

    private val currencyModule = Kodein.Module(name = "currencyModule") {
        bind() from singleton { CurrencyController(instance()) }
        bind() from singleton { CurrencyService(instance()) }
        bind() from singleton { CurrencyRepository() }
    }

    internal val kodein = Kodein {
        import(userModule)
        import(accountModule)
        import(recordModule)
        import(categoryModule)
        import(budgetModule)
        import(importModule)
        import(currencyModule)
    }
}