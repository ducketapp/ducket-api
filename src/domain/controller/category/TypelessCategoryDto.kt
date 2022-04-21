package io.ducket.api.domain.controller.category

import domain.model.category.Category

data class TypelessCategoryDto(
    val id: Long,
    val name: String,
) {
    constructor(category: Category): this(
        id = category.id,
        name = category.name,
    )
}