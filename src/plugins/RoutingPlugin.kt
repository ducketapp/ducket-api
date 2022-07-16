package dev.ducket.api.plugins

import dev.ducket.api.domain.controller.account.AccountController
import dev.ducket.api.domain.controller.periodic_budget.PeriodicBudgetController
import dev.ducket.api.domain.controller.budget.BudgetController
import dev.ducket.api.domain.controller.category.CategoryController
import dev.ducket.api.domain.controller.currency.CurrencyController
import dev.ducket.api.domain.controller.imports.ImportController
import dev.ducket.api.domain.controller.operation.OperationController
import dev.ducket.api.domain.controller.rule.ImportRuleController
import dev.ducket.api.domain.controller.tag.TagController
import dev.ducket.api.domain.controller.user.UserController
import dev.ducket.api.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.installRoutingPlugin() {
    val userController by inject<UserController>()
    val accountController by inject<AccountController>()
    val categoryController by inject<CategoryController>()
    val budgetController by inject<BudgetController>()
    val periodicBudgetController by inject<PeriodicBudgetController>()
    val currencyController by inject<CurrencyController>()
    val importController by inject<ImportController>()
    val importRuleController by inject<ImportRuleController>()
    val operationController by inject<OperationController>()
    val tagController by inject<TagController>()

    install(Routing) {
        route("/api") {
            healthCheck()
            currencies(currencyController)
            users(userController)
            accounts(accountController)
            categories(categoryController)
            budgets(budgetController)
            periodicBudgets(periodicBudgetController)
            imports(importController)
            importRules(importRuleController)
            operations(operationController)
            tags(tagController)
        }
    }
}