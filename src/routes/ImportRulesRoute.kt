package io.ducket.api.routes

import io.ducket.api.domain.controller.rule.ImportRuleController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.importRules(
    importRuleController: ImportRuleController
) {
    authenticate {
        route("/rules") {
            post { importRuleController.createImportRule(this.context) }
            get { importRuleController.getImportRules(this.context) }
            // delete { importRuleController.deleteImportRules(this.context) }

            route("/{ruleId}") {
                get { importRuleController.getImportRule(this.context) }
                put { importRuleController.updateImportRule(this.context) }
                delete { importRuleController.deleteImportRule(this.context) }
            }
        }
    }
}