package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.controller.tag.dto.TagCreateUpdateDto
import dev.ducket.api.domain.controller.tag.dto.TagDto
import dev.ducket.api.domain.model.tag.Tag
import dev.ducket.api.domain.model.tag.TagCreate
import dev.ducket.api.domain.model.tag.TagUpdate

object TagMapper {

    fun mapDtoToModel(dto: TagCreateUpdateDto, userId: Long): TagCreate {
        return DataClassMapper<TagCreateUpdateDto, TagCreate>()
            .provide(TagCreate::userId, userId)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: TagCreateUpdateDto): TagUpdate {
        return DataClassMapper<TagCreateUpdateDto, TagUpdate>().invoke(dto)
    }

    fun mapModelToDto(model: Tag): TagDto {
        return DataClassMapper<Tag, TagDto>().invoke(model)
    }
}