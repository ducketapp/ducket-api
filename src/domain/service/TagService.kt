package io.ducket.api.domain.service

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.tag.TagCreateDto
import io.ducket.api.domain.controller.tag.TagDto
import io.ducket.api.domain.controller.tag.TagUpdateDto
import io.ducket.api.domain.repository.TagRepository
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException

class TagService(private val tagRepository: TagRepository) {

    suspend fun createTag(userId: Long, reqObj: TagCreateDto): TagDto {
        tagRepository.findOneByName(userId, reqObj.name)?.also {
            throw DuplicateDataException()
        }

        return TagDto(tagRepository.createOne(userId, reqObj))
    }

    suspend fun updateTag(userId: Long, tagId: Long, reqObj: TagUpdateDto): TagDto {
        tagRepository.findOneByName(userId, reqObj.name)?.also {
            throw DuplicateDataException()
        }

        return tagRepository.updateOne(userId, tagId, reqObj)?.let { TagDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun getTag(userId: Long, tagId: Long): TagDto {
        return tagRepository.findOne(userId, tagId)?.let { TagDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun getTags(userId: Long): List<TagDto> {
        return tagRepository.findAll(userId).map { TagDto(it) }
    }

    suspend fun deleteTag(userId: Long, tagId: Long) {
        tagRepository.delete(userId, tagId)
    }

    suspend fun deleteTags(userId: Long, reqObj: BulkDeleteDto) {
        tagRepository.delete(userId, *reqObj.ids.toLongArray())
    }
}