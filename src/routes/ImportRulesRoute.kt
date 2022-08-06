package dev.ducketapp.service.routes

import dev.ducketapp.service.domain.controller.rule.ImportRuleController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

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