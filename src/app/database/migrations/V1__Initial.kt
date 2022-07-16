package dev.ducket.api.app.database.migrations

import dev.ducket.api.domain.model.account.AccountsTable
import dev.ducket.api.domain.model.category.CategoriesTable
import dev.ducket.api.domain.model.currency.CurrenciesTable
import dev.ducket.api.domain.model.imports.ImportRulesTable
import dev.ducket.api.domain.model.imports.ImportsTable
import dev.ducket.api.domain.model.user.UsersTable
import dev.ducket.api.domain.model.periodic_budget.PeriodicBudgetAccountsTable
import dev.ducket.api.domain.model.periodic_budget.PeriodicBudgetsTable
import dev.ducket.api.domain.model.operation.OperationsTable
import dev.ducket.api.domain.model.periodic_budget.PeriodicBudgetLimitsTable
import dev.ducket.api.domain.model.budget.BudgetAccountsTable
import dev.ducket.api.domain.model.budget.BudgetsTable
import dev.ducket.api.domain.model.currency.CurrencyRatesTable
import dev.ducket.api.domain.model.operation.OperationTagsTable
import dev.ducket.api.domain.model.tag.TagsTable
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