package io.budgery.api.domain.service

import io.budgery.api.domain.controller.label.LabelCreateDto
import io.budgery.api.domain.controller.label.LabelDto
import io.budgery.api.domain.repository.LabelRepository

class LabelService(private val labelRepository: LabelRepository) {

    fun createLabel(userId: Int, reqObj: LabelCreateDto) : LabelDto {
        labelRepository.findOneByNameAndUserId(reqObj.name, userId)?.let {
            throw IllegalArgumentException("'${reqObj.name}' label already exists")
        }

        val newLabel = labelRepository.create(userId, reqObj)

        return LabelDto(newLabel)
    }

    fun getLabels(userId: Int) : List<LabelDto> {
        return labelRepository.findAllByUserId(userId).map { LabelDto(it) }
    }
}