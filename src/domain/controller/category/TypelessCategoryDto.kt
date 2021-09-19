package io.budgery.api.domain.controller.category

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.model.category.Category

class TypelessCategoryDto(@JsonIgnore val category: Category) {
    val id = category.id
    val name = category.name
}