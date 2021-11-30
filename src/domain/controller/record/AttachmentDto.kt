package io.ducket.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import io.ducket.api.domain.model.attachment.Attachment

class AttachmentDto(@JsonIgnore val attachment: Attachment) {
    val id: String = attachment.id
    val fileName: String = attachment.filePath.substringAfterLast("\\")
}