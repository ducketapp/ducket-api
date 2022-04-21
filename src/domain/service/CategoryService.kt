package io.ducket.api.domain.service

import io.ducket.api.domain.controller.category.TypedCategoryDto
import io.ducket.api.domain.controller.category.GroupedCategoryDto
import io.ducket.api.domain.controller.category.TypelessCategoryDto
import io.ducket.api.domain.repository.CategoryRepository
import io.ducket.api.plugins.NoEntityFoundException

class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getCategories(): List<GroupedCategoryDto> {
        return categoryRepository.findAll()
            .groupByTo(LinkedHashMap()) { it.group }
            .map { grouped ->
                GroupedCategoryDto(grouped.key,  grouped.value.map { TypelessCategoryDto(it) })
            }
    }

    fun getCategory(id: Long): TypedCategoryDto {
        return categoryRepository.findOne(id)?.let { TypedCategoryDto(it) } ?: throw NoEntityFoundException()
    }
}