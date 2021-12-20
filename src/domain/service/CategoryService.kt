package io.ducket.api.domain.service

import io.ducket.api.domain.controller.category.TypedCategoryDto
import io.ducket.api.domain.controller.category.GroupedCategoryDto
import io.ducket.api.domain.repository.CategoryRepository
import io.ducket.api.plugins.NoEntityFoundError

class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getCategories(): List<GroupedCategoryDto> {
        return categoryRepository.findAll()
            .groupByTo(LinkedHashMap()) { it.group }
            .map { GroupedCategoryDto(it.key, it.value) }
    }

    fun getCategory(id: Long): TypedCategoryDto {
        return categoryRepository.findById(id)?.let { TypedCategoryDto(it) }
            ?: throw NoEntityFoundError("No such category was found")
    }
}