package dev.ducket.api.routes

import dev.ducket.api.auth.authentication.UserRole
import dev.ducket.api.auth.authorization.authorize
import dev.ducket.api.domain.controller.imports.ImportController
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