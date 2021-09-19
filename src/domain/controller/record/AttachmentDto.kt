package io.budgery.api.domain.controller.record

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.model.transaction.Transaction
import io.budgery.api.domain.controller.label.LabelDto
import io.budgery.api.domain.controller.transaction.TransactionRuleDto
import io.budgery.api.domain.model.attachment.Attachment

class AttachmentDto(@JsonIgnore val attachment: Attachment) {
    val id: Int = attachment.id
    val originalFileName: String = attachment.originalFileName
}