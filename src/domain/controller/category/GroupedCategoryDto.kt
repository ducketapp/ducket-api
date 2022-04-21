package io.ducket.api.domain.controller.category

import io.ducket.api.app.CategoryTypeGroup

data class GroupedCategoryDto(
    val group: CategoryTypeGroup,
    val groupCategories: List<TypelessCategoryDto>,
)