package dev.ducketapp.service.domain.service

import dev.ducketapp.service.domain.mapper.CategoryMapper
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto
import dev.ducketapp.service.domain.repository.CategoryRepository
import dev.ducketapp.service.plugins.NoDataFoundException

class CategoryService(private val categoryRepository: CategoryRepository) {

    suspend fun getCategories(): List<CategoryDto> {
        return categoryRepository.findAll().map { CategoryMapper.mapModelToDto(it) }
    }

    suspend fun getCategory(id: Long): CategoryDto {
        return categoryRepository.findOne(id)?.let { CategoryMapper.mapModelToDto(it) } ?: throw NoDataFoundException()
    }
}