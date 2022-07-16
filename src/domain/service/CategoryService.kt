package dev.ducket.api.domain.service

import dev.ducket.api.domain.mapper.CategoryMapper
import dev.ducket.api.domain.controller.category.dto.CategoryDto
import dev.ducket.api.domain.repository.CategoryRepository
import dev.ducket.api.plugins.NoDataFoundException

class CategoryService(private val categoryRepository: CategoryRepository) {

    suspend fun getCategories(): List<CategoryDto> {
        return categoryRepository.findAll().map { CategoryMapper.mapModelToDto(it) }
    }

    suspend fun getCategory(id: Long): CategoryDto {
        return categoryRepository.findOne(id)?.let { CategoryMapper.mapModelToDto(it) } ?: throw NoDataFoundException()
    }
}