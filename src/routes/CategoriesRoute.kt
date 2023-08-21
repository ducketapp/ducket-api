package org.expenny.service.routes

import org.expenny.service.domain.controller.category.CategoryController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.categories(categoryController: CategoryController) {
    authenticate {
        route("/categories") {
            get { categoryController.getCategories(this.context) }

            route("/{categoryId}") {
                get { categoryController.getCategory(this.context) }
            }
        }
    }
}