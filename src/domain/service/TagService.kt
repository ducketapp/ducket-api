package org.expenny.service.domain.service

import org.expenny.service.domain.mapper.TagMapper
import org.expenny.service.domain.controller.BulkDeleteDto
import org.expenny.service.domain.controller.tag.dto.TagCreateUpdateDto
import org.expenny.service.domain.controller.tag.dto.TagDto
import org.expenny.service.domain.repository.TagRepository
import org.expenny.service.plugins.DuplicateDataException
import org.expenny.service.plugins.NoDataFoundException

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