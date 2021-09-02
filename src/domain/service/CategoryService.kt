package io.budgery.api.domain.service

import io.budgery.api.domain.controller.category.CompleteCategoryDto
import io.budgery.api.domain.controller.category.GroupedCategoryDto
import io.budgery.api.domain.repository.CategoryRepository

class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getCategories(): List<GroupedCategoryDto> {
        val allCategories = categoryRepository.findAll()
        return allCategories.groupByTo(LinkedHashMap()) { it.categoryType }.map { GroupedCategoryDto(it.key, it.value) }
    }

    fun getCategory(id: Int): CompleteCategoryDto {
        return categoryRepository.findById(id)?.let { CompleteCategoryDto(it) }
            ?: throw NoSuchElementException("No such category was found")
    }
}