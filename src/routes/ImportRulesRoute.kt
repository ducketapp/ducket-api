package io.ducket.api.routes

import io.ducket.api.domain.controller.rule.ImportRuleController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.importRules(importRuleController: ImportRuleController) {
    authenticate {
        route("/import-rules") {
            post { importRuleController.createImportRule(this.context) }
            get { importRuleController.getImportRules(this.context) }
            delete { importRuleController.deleteImportRules(this.context) }

            route("/{importRuleId}") {
                get { importRuleController.getImportRule(this.context) }
                put { importRuleController.updateImportRule(this.context) }
                delete { importRuleController.deleteImportRule(this.context) }
            }
        }
    }
}