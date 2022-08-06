package dev.ducketapp.service.plugins

import dev.ducketapp.service.domain.controller.account.AccountController
import dev.ducketapp.service.domain.controller.periodic_budget.PeriodicBudgetController
import dev.ducketapp.service.domain.controller.budget.BudgetController
import dev.ducketapp.service.domain.controller.category.CategoryController
import dev.ducketapp.service.domain.controller.currency.CurrencyController
import dev.ducketapp.service.domain.controller.imports.ImportController
import dev.ducketapp.service.domain.controller.operation.OperationController
import dev.ducketapp.service.domain.controller.rule.ImportRuleController
import dev.ducketapp.service.domain.controller.tag.TagController
import dev.ducketapp.service.domain.controller.user.UserController
import dev.ducketapp.service.routes.*
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