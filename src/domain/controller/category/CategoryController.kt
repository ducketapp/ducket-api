package dev.ducketapp.service.domain.controller.category

import dev.ducketapp.service.domain.service.CategoryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.util.*

class CategoryController(
    private val categoryService: CategoryService,
) {

    suspend fun getCategories(ctx: ApplicationCall) {
        val categories = categoryService.getCategories()

        ctx.respond(HttpStatusCode.OK, categories)
    }

    suspend fun getCategory(ctx: ApplicationCall) {
        val categoryId = ctx.parameters.getOrFail("categoryId").toLong()

        val categoryDto = categoryService.getCategory(categoryId)
        ctx.respond(HttpStatusCode.OK, categoryDto)
    }
}