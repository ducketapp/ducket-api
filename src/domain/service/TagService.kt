package io.ducket.api.domain.service

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.tag.TagCreateDto
import io.ducket.api.domain.controller.tag.TagDto
import io.ducket.api.domain.controller.tag.TagUpdateDto
import io.ducket.api.domain.repository.TagRepository
import io.ducket.api.plugins.DuplicateEntityException
import io.ducket.api.plugins.NoEntityFoundException

class TagService(
    private val tagRepository: TagRepository,
) {

    fun createTag(userId: Long, payload: TagCreateDto): TagDto {
        tagRepository.findOneByName(userId, payload.name)?.run {
            throw DuplicateEntityException()
        }

        return TagDto(tagRepository.createOne(userId, payload))
    }

    fun updateTag(userId: Long, tagId: Long, payload: TagUpdateDto): TagDto {
        tagRepository.findOneByName(userId, payload.name)?.run {
            throw DuplicateEntityException()
        }

        return TagDto(tagRepository.updateOne(userId, tagId, payload))
    }

    fun getTag(userId: Long, tagId: Long): TagDto {
        return tagRepository.findOne(userId, tagId)?.let { TagDto(it) } ?: throw NoEntityFoundException()
    }

    fun getTags(userId: Long): List<TagDto> {
        return tagRepository.findAll(userId).map { TagDto(it) }
    }

    fun deleteTag(userId: Long, tagId: Long) {
        tagRepository.delete(userId, tagId)
    }

    fun deleteTags(userId: Long, payload: BulkDeleteDto) {
        tagRepository.delete(userId, *payload.ids.toLongArray())
    }
}