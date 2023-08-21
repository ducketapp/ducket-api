package org.expenny.service.plugins

import org.expenny.service.domain.controller.account.AccountController
import org.expenny.service.domain.controller.periodic_budget.PeriodicBudgetController
import org.expenny.service.domain.controller.budget.BudgetController
import org.expenny.service.domain.controller.category.CategoryController
import org.expenny.service.domain.controller.currency.CurrencyController
import org.expenny.service.domain.controller.imports.ImportController
import org.expenny.service.domain.controller.operation.OperationController
import org.expenny.service.domain.controller.rule.ImportRuleController
import org.expenny.service.domain.controller.tag.TagController
import org.expenny.service.domain.controller.user.UserController
import org.expenny.service.routes.*
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