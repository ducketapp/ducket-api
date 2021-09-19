package io.budgery.api.config

import io.budgery.api.domain.controller.account.AccountController
import io.budgery.api.domain.controller.budget.BudgetController
import io.budgery.api.domain.controller.category.CategoryController
import io.budgery.api.domain.controller.label.LabelController
import io.budgery.api.domain.controller.record.RecordController
import io.budgery.api.domain.controller.transaction.TransactionController
import io.budgery.api.domain.controller.transfer.TransferController
import io.budgery.api.domain.controller.user.UserController
import io.budgery.api.domain.repository.*
import io.budgery.api.domain.service.*
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

object DependencyInjectionConfig {

    private val userModule = Kodein.Module(name = "userModule") {
        bind() from singleton { UserController(instance(), instance(), instance()) }
        bind() from singleton { UserService(instance(), instance()) }
        bind() from singleton { UserRepository() }
    }

    private val accountModule = Kodein.Module(name = "accountModule") {
        bind() from singleton { AccountController(instance()) }
        bind() from singleton { AccountService(instance(), instance(), instance()) }
        bind() from singleton { AccountRepository() }
        bind() from singleton { CurrencyRepository() }
    }

    private val recordModule = Kodein.Module(name = "recordModule") {
        bind() from singleton { RecordController(instance(), instance(), instance()) }
        bind() from singleton { TransactionController(instance(), instance()) }
        bind() from singleton { TransferController(instance(), instance()) }
        bind() from singleton { TransactionService(instance()) }
        bind() from singleton { TransferService(instance(), instance()) }
        bind() from singleton { TransactionRepository() }
        bind() from singleton { TransferRepository() }
    }

    private val labelModule = Kodein.Module(name = "labelModule") {
        bind() from singleton { LabelController(instance()) }
        bind() from singleton { LabelService(instance()) }
        bind() from singleton { LabelRepository() }
    }

    private val categoryModule = Kodein.Module(name = "categoryModule") {
        bind() from singleton { CategoryController(instance()) }
        bind() from singleton { CategoryService(instance()) }
        bind() from singleton { CategoryRepository() }
    }

    private val budgetModule = Kodein.Module(name = "budgetModule") {
        bind() from singleton { BudgetController(instance()) }
        bind() from singleton { BudgetService(instance(), instance(), instance(), instance(), instance(), instance()) }
        bind() from singleton { BudgetRepository() }
    }

    private val importModule = Kodein.Module(name = "importModule") {
        bind() from singleton { ImportService(instance()) }
        bind() from singleton { ImportRepository() }
    }

    internal val kodein = Kodein {
        import(userModule)
        import(accountModule)
        import(recordModule)
        import(labelModule)
        import(categoryModule)
        import(budgetModule)
        import(importModule)
    }
}