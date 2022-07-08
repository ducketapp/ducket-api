package io.ducket.api.routes

import io.ducket.api.auth.UserRole
import io.ducket.api.auth.authorization.authorize
import io.ducket.api.domain.controller.imports.ImportController
import io.ktor.auth.*
import io.ktor.routing.*

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