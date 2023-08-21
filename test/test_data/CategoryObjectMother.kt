package org.expenny.service.test_data

import org.expenny.service.domain.model.category.Category

class CategoryObjectMother {
    companion object {

        fun category() = Category(
            id = 1L,
            name = "Books",
            group = "Shopping",
        )
    }
}