package dev.ducketapp.service.app.database.migrations

import dev.ducketapp.service.domain.model.account.AccountsTable
import dev.ducketapp.service.domain.model.category.CategoriesTable
import dev.ducketapp.service.domain.model.currency.CurrenciesTable
import dev.ducketapp.service.domain.model.imports.ImportRulesTable
import dev.ducketapp.service.domain.model.imports.ImportsTable
import dev.ducketapp.service.domain.model.user.UsersTable
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetAccountsTable
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetsTable
import dev.ducketapp.service.domain.model.operation.OperationsTable
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetLimitsTable
import dev.ducketapp.service.domain.model.budget.BudgetAccountsTable
import dev.ducketapp.service.domain.model.budget.BudgetsTable
import dev.ducketapp.service.domain.model.currency.CurrencyRatesTable
import dev.ducketapp.service.domain.model.operation.OperationTagsTable
import dev.ducketapp.service.domain.model.tag.TagsTable
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused", "ClassName")
open class V1__Initial : BaseJavaMigration() {

    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                tables = arrayOf(
                    CurrenciesTable,
                    CurrencyRatesTable,
                    UsersTable,
                    AccountsTable,
                    CategoriesTable,
                    ImportsTable,
                    ImportRulesTable,
                    BudgetsTable,
                    BudgetAccountsTable,
                    PeriodicBudgetsTable,
                    PeriodicBudgetLimitsTable,
                    PeriodicBudgetAccountsTable,
                    OperationsTable,
                    TagsTable,
                    OperationTagsTable,
                ),
                inBatch = false,
                withLogs = true,
            )
        }
    }
}