package domain.mapper

import domain.model.category.Category
import io.ducket.api.domain.controller.category.dto.CategoryDto

object CategoryMapper {

    fun mapModelToDto(model: Category): CategoryDto {
        return DataClassMapper<Category, CategoryDto>().invoke(model)
    }
}