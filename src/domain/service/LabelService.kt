package io.ducket.api.domain.service

import io.ducket.api.domain.controller.label.LabelCreateDto
import io.ducket.api.domain.controller.label.LabelDto
import io.ducket.api.domain.repository.LabelRepository
import io.ducket.api.plugins.InvalidDataError

class LabelService(private val labelRepository: LabelRepository) {

    fun createLabel(userId: String, reqObj: LabelCreateDto): LabelDto {
        labelRepository.findOneByNameAndUserId(userId, reqObj.name)?.let {
            throw InvalidDataError("'${reqObj.name}' label already exists")
        }

        val newLabel = labelRepository.create(userId, reqObj)

        return LabelDto(newLabel)
    }

    fun getLabels(userId: String): List<LabelDto> {
        return labelRepository.findAllByUserId(userId).map { LabelDto(it) }
    }
}