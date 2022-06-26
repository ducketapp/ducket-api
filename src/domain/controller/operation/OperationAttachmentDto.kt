package io.ducket.api.domain.controller.operation

import io.ducket.api.domain.model.attachment.Attachment

data class OperationAttachmentDto(
    val id: Long,
    val imageName: String,
) {
    constructor(attachment: Attachment): this(
        id = attachment.id,
        imageName = attachment.filePath.substringAfterLast("\\"),
    )
}