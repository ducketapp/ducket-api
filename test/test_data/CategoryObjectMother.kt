package io.ducket.api.test_data

import domain.model.category.Category
import io.ducket.api.app.CategoryType
import io.ducket.api.app.CategoryTypeGroup

class CategoryObjectMother {
    companion object {

        fun category() = Category(
            id = 1L,
            name = CategoryType.GROCERY.name,
            group = CategoryTypeGroup.FOOD_AND_DRINKS,
        )
    }
}