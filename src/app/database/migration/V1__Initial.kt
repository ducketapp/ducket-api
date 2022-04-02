package io.ducket.api.app.database.migration

import domain.model.account.AccountsTable
import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import domain.model.imports.ImportRulesTable
import domain.model.imports.ImportsTable
import domain.model.transaction.TransactionsTable
import domain.model.user.UsersTable
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.budget.BudgetAccountsTable
import io.ducket.api.domain.model.budget.BudgetCategoriesTable
import io.ducket.api.domain.model.budget.BudgetsTable
import io.ducket.api.domain.model.group.GroupMembershipsTable
import io.ducket.api.domain.model.group.GroupsTable
import io.ducket.api.domain.model.transaction.TransactionAttachmentsTable
import io.ducket.api.domain.model.transfer.TransferAttachmentsTable
import io.ducket.api.domain.model.transfer.TransfersTable
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused", "ClassName")
open class V1__Initial : BaseJavaMigration() {

    override fun migrate(context: Context?) {
        transaction {
            // SchemaUtils.createSchema(Schema("ducket-db"))

            SchemaUtils.createMissingTablesAndColumns(
                tables = arrayOf(
                    CurrenciesTable,
                    UsersTable,
                    AccountsTable,
                    CategoriesTable,
                    ImportsTable,
                    TransactionsTable,
                    ImportRulesTable,
                    BudgetsTable,
                    TransfersTable,
                    BudgetAccountsTable,
                    BudgetCategoriesTable,
                    AttachmentsTable,
                    TransactionAttachmentsTable,
                    TransferAttachmentsTable,
                    GroupsTable,
                    GroupMembershipsTable
                ),
                inBatch = false,
                withLogs = true,
            )
        }
    }
}