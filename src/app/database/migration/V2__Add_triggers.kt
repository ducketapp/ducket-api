package io.ducket.api.app.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused", "ClassName")
class V2__Add_triggers: BaseJavaMigration() {

    override fun migrate(context: Context?) {
        transaction {
            // Add triggers for many-to-many relations
            val connection = TransactionManager.current().connection

            val attachmentBeforeDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `attachment_BEFORE_DELETE` BEFORE DELETE ON `attachment` FOR EACH ROW
                BEGIN
                	DELETE FROM `operation_attachment` WHERE `operation_attachment`.`attachment_id` = OLD.`id`;
                END;;
            """.trimIndent()

            val groupBeforeDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `group_BEFORE_DELETE` BEFORE DELETE ON `group` FOR EACH ROW
                BEGIN
                	DELETE FROM `group_membership` WHERE `group_membership`.`group_id` = OLD.`id`;
                END;;
            """.trimIndent()

            val budgetBeforeDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `budget_BEFORE_DELETE` BEFORE DELETE ON `budget` FOR EACH ROW
                BEGIN
                	DELETE FROM `budget_account` WHERE `budget_account`.`budget_id` = OLD.`id`;
                    DELETE FROM `budget_category` WHERE `budget_category`.`budget_id` = OLD.`id`;
                END;;
            """.trimIndent()

            connection.prepareStatement(attachmentBeforeDeleteTrigger, false).executeUpdate()
            connection.prepareStatement(groupBeforeDeleteTrigger, false).executeUpdate()
            connection.prepareStatement(budgetBeforeDeleteTrigger, false).executeUpdate()
        }
    }
}