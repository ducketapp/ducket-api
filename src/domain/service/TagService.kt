package io.ducket.api.domain.service

import domain.mapper.TagMapper
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.tag.dto.TagCreateUpdateDto
import io.ducket.api.domain.controller.tag.dto.TagDto
import io.ducket.api.domain.repository.TagRepository
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException

class TagService(private val tagRepository: TagRepository) {

    suspend fun createTag(userId: Long, dto: TagCreateUpdateDto): TagDto {
        tagRepository.findOneByTitle(userId, dto.title)?.also { throw DuplicateDataException() }

        return tagRepository.create(TagMapper.mapDtoToModel(dto, userId)).let {
            TagMapper.mapModelToDto(it)
        }
    }

    suspend fun updateTag(userId: Long, tagId: Long, dto: TagCreateUpdateDto): TagDto {
        tagRepository.findOneByTitle(userId, dto.title)?.also { throw DuplicateDataException() }

        return tagRepository.update(userId, tagId,  TagMapper.mapDtoToModel(dto))?.let {
            TagMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getTag(userId: Long, tagId: Long): TagDto {
        return tagRepository.findOne(userId, tagId)?.let {
            TagMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun getTags(userId: Long): List<TagDto> {
        return tagRepository.findAll(userId).map { TagMapper.mapModelToDto(it) }
    }

    suspend fun deleteTag(userId: Long, tagId: Long) {
        tagRepository.delete(userId, tagId)
    }

    suspend fun deleteTags(userId: Long, dto: BulkDeleteDto) {
        tagRepository.delete(userId, *dto.ids.toLongArray())
    }
}