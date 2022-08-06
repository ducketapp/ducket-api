package dev.ducketapp.service.test_data

import dev.ducketapp.service.domain.model.category.Category

class CategoryObjectMother {
    companion object {

        fun category() = Category(
            id = 1L,
            name = "Books",
            group = "Shopping",
        )
    }
}