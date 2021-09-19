package io.budgery.api.domain.controller.category

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.category.Category
import domain.model.category.CategoryType

class TypedCategoryDto(@JsonIgnore val category: Category) {
    val id: Int = category.id
    val name: String = category.name
    val categoryType: CategoryType = category.categoryType
}