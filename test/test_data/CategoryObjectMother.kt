package io.ducket.api.test_data

import io.ducket.api.domain.model.category.Category

class CategoryObjectMother {
    companion object {

        fun category() = Category(
            id = 1L,
            name = "Books",
            group = "Shopping",
        )
    }
}