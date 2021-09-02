package io.budgery.api.domain.controller.category

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.category.Category
import domain.model.category.CategoryType

class GroupedCategoryDto(
    @JsonUnwrapped val categoryType: CategoryType,
    @JsonIgnore var categories: List<Category>,
) {
    val nestedCategories: List<Any> = categories.map { CategoryDto(it) }
}