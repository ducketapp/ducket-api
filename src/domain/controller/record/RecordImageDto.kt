package io.ducket.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.ducket.api.domain.model.attachment.Attachment

@JsonPropertyOrder(value = ["id"])
open class RecordImageDto(
    @JsonIgnore val attachment: Attachment,
) {
    val id: Long = attachment.id
    val imageName: String = attachment.filePath.substringAfterLast("\\")
}