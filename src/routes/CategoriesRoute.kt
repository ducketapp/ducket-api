package io.ducket.api.routes

import io.ducket.api.domain.controller.category.CategoryController
import io.ktor.auth.*
import io.ktor.routing.*

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