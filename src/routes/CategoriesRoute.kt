package dev.ducket.api.routes

import dev.ducket.api.domain.controller.category.CategoryController
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