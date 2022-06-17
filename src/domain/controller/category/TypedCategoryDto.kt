package io.ducket.api.domain.controller.category

import domain.model.category.Category

data class TypedCategoryDto(
    val id: Long,
    val name: String,
    val group: String,
) {
    constructor(category: Category): this(
        id = category.id,
        name = category.name,
        group = category.group,
    )
}