package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.category.Category
import dev.ducket.api.domain.controller.category.dto.CategoryDto

object CategoryMapper {

    fun mapModelToDto(model: Category): CategoryDto {
        return DataClassMapper<Category, CategoryDto>().invoke(model)
    }
}