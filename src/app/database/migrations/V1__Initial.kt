package io.ducket.api.app.database.migrations

import domain.model.account.AccountsTable
import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import domain.model.imports.ImportRulesTable
import domain.model.imports.ImportsTable
import domain.model.user.UsersTable
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.budget.BudgetAccountsTable
import io.ducket.api.domain.model.budget.BudgetCategoriesTable
import io.ducket.api.domain.model.budget.BudgetsTable
import io.ducket.api.domain.model.ledger.LedgerRecordsTable
import domain.model.operation.OperationAttachmentsTable
import domain.model.operation.OperationsTable
import io.ducket.api.domain.model.currency.CurrencyRatesTable
import io.ducket.api.domain.model.group.GroupMemberAccountPermissionsTable
import io.ducket.api.domain.model.group.GroupMembershipsTable
import io.ducket.api.domain.model.group.GroupsTable
import io.ducket.api.domain.model.operation.OperationTagsTable
import io.ducket.api.domain.model.tag.TagsTable
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
                    AttachmentsTable,
                    BudgetAccountsTable,
                    BudgetCategoriesTable,
                    GroupsTable,
                    GroupMembershipsTable,
                    GroupMemberAccountPermissionsTable,
                    LedgerRecordsTable,
                    OperationsTable,
                    OperationAttachmentsTable,
                    TagsTable,
                    OperationTagsTable,
                ),
                inBatch = false,
                withLogs = true,
            )
        }
    }
}