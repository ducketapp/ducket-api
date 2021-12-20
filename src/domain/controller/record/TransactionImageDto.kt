package io.ducket.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import io.ducket.api.domain.model.attachment.Attachment

class TransactionImageDto(
    @JsonIgnore val transactionId: Long,
    attachment: Attachment,
): RecordImageDto(attachment) {
    val imageUrlPath: String = "transactions/$transactionId/images/${super.id}"
}