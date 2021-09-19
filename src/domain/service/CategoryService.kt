package io.budgery.api.domain.service

import io.budgery.api.domain.controller.category.TypedCategoryDto
import io.budgery.api.domain.controller.category.GroupedCategoryDto
import io.budgery.api.domain.repository.CategoryRepository

class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getCategories(): List<GroupedCategoryDto> {
        return categoryRepository.findAll()
            .groupByTo(LinkedHashMap()) { it.categoryType }
            .map { GroupedCategoryDto(it.key, it.value) }
    }

    fun getCategory(id: Int): TypedCategoryDto {
        return categoryRepository.findById(id)?.let { TypedCategoryDto(it) }
            ?: throw NoSuchElementException("No such category was found")
    }
}