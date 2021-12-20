package io.ducket.api.domain.controller.category

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.model.category.Category
import domain.model.category.CategoryGroup

data class TypedCategoryDto(@JsonIgnore val category: Category) {
    val id: Long = category.id
    val name: String = category.name
    val group: CategoryGroup = category.group
}