package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.category.Category
import org.expenny.service.domain.controller.category.dto.CategoryDto

object CategoryMapper {

    fun mapModelToDto(model: Category): CategoryDto {
        return DataClassMapper<Category, CategoryDto>().invoke(model)
    }
}