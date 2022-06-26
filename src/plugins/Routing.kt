package io.ducket.api.plugins

import io.ducket.api.domain.controller.account.AccountController
import io.ducket.api.domain.controller.budget.BudgetController
import io.ducket.api.domain.controller.category.CategoryController
import io.ducket.api.domain.controller.currency.CurrencyController
import io.ducket.api.domain.controller.group.GroupController
import io.ducket.api.domain.controller.operation.OperationController
import io.ducket.api.domain.controller.rule.ImportRuleController
import io.ducket.api.domain.controller.tag.TagController
import io.ducket.api.domain.controller.user.UserController
import io.ducket.api.routes.*
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.installRouting() {
    val userController by inject<UserController>()
    val accountController by inject<AccountController>()
    val categoryController by inject<CategoryController>()
    val budgetController by inject<BudgetController>()
    val currencyController by inject<CurrencyController>()
    val groupController by inject<GroupController>()
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
            groups(groupController)
            importRules(importRuleController)
            operations(operationController)
            tags(tagController)
        }
    }
}