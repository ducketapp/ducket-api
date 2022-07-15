package io.ducket.api.domain.service

import io.ducket.api.domain.mapper.CategoryMapper
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.repository.CategoryRepository
import io.ducket.api.plugins.NoDataFoundException

class CategoryService(private val categoryRepository: CategoryRepository) {

    suspend fun getCategories(): List<CategoryDto> {
        return categoryRepository.findAll().map { CategoryMapper.mapModelToDto(it) }
    }

    suspend fun getCategory(id: Long): CategoryDto {
        return categoryRepository.findOne(id)?.let { CategoryMapper.mapModelToDto(it) } ?: throw NoDataFoundException()
    }
}