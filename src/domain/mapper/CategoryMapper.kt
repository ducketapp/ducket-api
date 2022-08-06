package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.category.Category
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto

object CategoryMapper {

    fun mapModelToDto(model: Category): CategoryDto {
        return DataClassMapper<Category, CategoryDto>().invoke(model)
    }
}