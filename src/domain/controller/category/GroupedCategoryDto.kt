package io.ducket.api.domain.controller.category

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.category.Category
import domain.model.category.CategoryGroup

data class GroupedCategoryDto(
    val group: CategoryGroup,
    @JsonIgnore var categories: List<Category>,
) {
    val groupCategories: List<Any> = categories.map { TypelessCategoryDto(it) }
}