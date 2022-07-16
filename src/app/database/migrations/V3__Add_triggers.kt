package dev.ducket.api.app.database.migrations

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused", "ClassName")
class V3__Add_triggers: BaseJavaMigration() {

    override fun migrate(context: Context?) {
        transaction {
            val connection = TransactionManager.current().connection

            val userBeforeUpdateTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `user_before_update` BEFORE UPDATE ON `user` FOR EACH ROW
                BEGIN
                    SET NEW.modified_at = CURRENT_TIMESTAMP(3);
                END;;
            """.trimIndent()

            val tagBeforeUpdateTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `tag_before_update` BEFORE UPDATE ON `tag` FOR EACH ROW
                BEGIN
                    SET NEW.modified_at = CURRENT_TIMESTAMP(3);
                END;;
            """.trimIndent()

            val operationBeforeUpdateTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `operation_before_update` BEFORE UPDATE ON `operation` FOR EACH ROW
                BEGIN
                    SET NEW.modified_at = CURRENT_TIMESTAMP(3);
                END;;
            """.trimIndent()

            val accountBeforeUpdateTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `account_before_update` BEFORE UPDATE ON `account` FOR EACH ROW
                BEGIN
                    SET NEW.modified_at = CURRENT_TIMESTAMP(3);
                END;;
            """.trimIndent()

            val operationBeforeDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `operation_before_delete` BEFORE DELETE ON `operation` FOR EACH ROW
                BEGIN
                    DELETE FROM `operation_tag` WHERE `operation_tag`.`operation_id` = OLD.`id`;
                END;;
            """.trimIndent()

            val operationTagAfterDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `operation_tag_after_delete` AFTER DELETE ON `operation_tag` FOR EACH ROW
                BEGIN
                	DELETE FROM `tag` WHERE `tag`.`id` = OLD.`tag_id`;
                END;;
            """.trimIndent()

            val budgetBeforeDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `budget_before_delete` BEFORE DELETE ON `budget` FOR EACH ROW
                BEGIN
                	DELETE FROM `budget_account` WHERE `budget_account`.`budget_id` = OLD.`id`;
                END;;
            """.trimIndent()

            val periodicBudgetBeforeDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `periodic_budget_before_delete` BEFORE DELETE ON `periodic_budget` FOR EACH ROW
                BEGIN
                	DELETE FROM `periodic_budget_account` WHERE `periodic_budget_account`.`budget_id` = OLD.`id`;
                    DELETE FROM `periodic_budget_limit` WHERE `periodic_budget_limit`.`budget_id` = OLD.`id`;
                END;;
            """.trimIndent()

            val importBeforeDeleteTrigger = """
                CREATE DEFINER = CURRENT_USER TRIGGER `import_before_delete` BEFORE DELETE ON `import` FOR EACH ROW
                BEGIN
                	DELETE FROM `operation` WHERE `operation`.`import_id` = OLD.`id`;
                END;;
            """.trimIndent()

            // Before update
            connection.prepareStatement(userBeforeUpdateTrigger, false).executeUpdate()
            connection.prepareStatement(tagBeforeUpdateTrigger, false).executeUpdate()
            connection.prepareStatement(operationBeforeUpdateTrigger, false).executeUpdate()
            connection.prepareStatement(accountBeforeUpdateTrigger, false).executeUpdate()

            // Before delete
            connection.prepareStatement(operationBeforeDeleteTrigger, false).executeUpdate()
            connection.prepareStatement(budgetBeforeDeleteTrigger, false).executeUpdate()
            connection.prepareStatement(periodicBudgetBeforeDeleteTrigger, false).executeUpdate()
            connection.prepareStatement(importBeforeDeleteTrigger, false).executeUpdate()

            // After delete
            connection.prepareStatement(operationTagAfterDeleteTrigger, false).executeUpdate()
        }
    }
}