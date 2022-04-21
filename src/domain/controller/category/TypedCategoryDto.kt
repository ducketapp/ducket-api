package io.ducket.api.domain.controller.category

import domain.model.category.Category
import io.ducket.api.app.CategoryTypeGroup

data class TypedCategoryDto(
    val id: Long,
    val name: String,
    val group: CategoryTypeGroup,
) {
    constructor(category: Category): this(
        id = category.id,
        name = category.name,
        group = category.group,
    )
}