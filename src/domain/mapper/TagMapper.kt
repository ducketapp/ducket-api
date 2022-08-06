package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.controller.tag.dto.TagCreateUpdateDto
import dev.ducketapp.service.domain.controller.tag.dto.TagDto
import dev.ducketapp.service.domain.model.tag.Tag
import dev.ducketapp.service.domain.model.tag.TagCreate
import dev.ducketapp.service.domain.model.tag.TagUpdate

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