package io.ducket.api.domain.controller.category

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.model.category.Category

data class TypelessCategoryDto(@JsonIgnore val category: Category) {
    val id: String = category.id
    val name: String = category.name
}