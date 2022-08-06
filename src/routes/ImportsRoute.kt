package dev.ducketapp.service.routes

import dev.ducketapp.service.auth.authentication.UserRole
import dev.ducketapp.service.auth.authorization.authorize
import dev.ducketapp.service.domain.controller.imports.ImportController
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