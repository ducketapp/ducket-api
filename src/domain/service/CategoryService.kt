package org.expenny.service.domain.service

import org.expenny.service.domain.mapper.CategoryMapper
import org.expenny.service.domain.controller.category.dto.CategoryDto
import org.expenny.service.domain.repository.CategoryRepository
import org.expenny.service.plugins.NoDataFoundException

class CategoryService(private val categoryRepository: CategoryRepository) {

    suspend fun getCategories(): List<CategoryDto> {
        return categoryRepository.findAll().map { CategoryMapper.mapModelToDto(it) }
    }

    suspend fun getCategory(id: Long): CategoryDto {
        return categoryRepository.findOne(id)?.let { CategoryMapper.mapModelToDto(it) } ?: throw NoDataFoundException()
    }
}