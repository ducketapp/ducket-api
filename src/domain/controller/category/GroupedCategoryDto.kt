package io.ducket.api.domain.controller.category

data class GroupedCategoryDto(
    val group: String,
    val groupCategories: List<TypelessCategoryDto>,
)