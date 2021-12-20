package io.ducket.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import io.ducket.api.domain.model.attachment.Attachment

class TransferImageDto(
    @JsonIgnore val transferId: Long,
    attachment: Attachment,
): RecordImageDto(attachment) {
    val imageUrlPath: String = "transfers/$transferId/images/${super.id}"
}