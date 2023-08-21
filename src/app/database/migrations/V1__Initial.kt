package org.expenny.service.app.database.migrations

import org.expenny.service.domain.model.account.AccountsTable
import org.expenny.service.domain.model.category.CategoriesTable
import org.expenny.service.domain.model.currency.CurrenciesTable
import org.expenny.service.domain.model.imports.ImportRulesTable
import org.expenny.service.domain.model.imports.ImportsTable
import org.expenny.service.domain.model.user.UsersTable
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetAccountsTable
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetsTable
import org.expenny.service.domain.model.operation.OperationsTable
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetLimitsTable
import org.expenny.service.domain.model.budget.BudgetAccountsTable
import org.expenny.service.domain.model.budget.BudgetsTable
import org.expenny.service.domain.model.currency.CurrencyRatesTable
import org.expenny.service.domain.model.operation.OperationTagsTable
import org.expenny.service.domain.model.tag.TagsTable
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