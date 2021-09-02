package io.budgery.api.domain.controller.category

import io.budgery.api.domain.service.CategoryService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*

class CategoryController(
    val categoryService: CategoryService,
) {

    suspend fun getCategories(ctx: ApplicationCall) {
        val categories = categoryService.getCategories()

        ctx.respond(HttpStatusCode.OK, categories)
    }

    suspend fun getCategory(ctx: ApplicationCall) {
        val categoryId = ctx.parameters.getOrFail("categoryId").toInt()

        val categoryDto = categoryService.getCategory(categoryId)
        ctx.respond(HttpStatusCode.OK, categoryDto)
    }
}