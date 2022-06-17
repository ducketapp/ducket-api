package io.ducket.api.test_data

import domain.model.category.Category
import io.ducket.api.app.DefaultCategory
import io.ducket.api.app.DefaultCategoryGroup

class CategoryObjectMother {
    companion object {

        fun category() = Category(
            id = 1L,
            name = DefaultCategory.GROCERY.name,
            group = DefaultCategoryGroup.FOOD,
        )
    }
}