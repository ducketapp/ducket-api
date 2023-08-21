package org.expenny.service.routes

import org.expenny.service.auth.authentication.UserRole
import org.expenny.service.auth.authorization.authorize
import org.expenny.service.domain.controller.imports.ImportController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.imports(importController: ImportController) {
    authenticate {
        authorize(UserRole.SUPER_USER) {
            route("/imports") {
                post { importController.createImport(this.context) }
                get { importController.getImports(this.context) }

                route("/{importId}") {
                    get { importController.getImport(this.context) }
                    put { importController.updateImport(this.context) }
                    delete { importController.deleteImport(this.context) }
                }
            }
        }
    }
}