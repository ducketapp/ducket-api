package io.ducket.api.domain.controller.operation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.domain.controller.category.TypedCategoryDto
import io.ducket.api.domain.controller.imports.ImportDto
import domain.model.operation.OperationModel
import io.ducket.api.app.OperationType
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.tag.TagDto
import io.ducket.api.utils.InstantDeserializer
import java.math.BigDecimal
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OperationDto(
    val id: Long,
    val import: ImportDto?,
    val transferAccount: AccountDto?,
    val account: AccountDto,
    val category: TypedCategoryDto?,
    val type: OperationType,
    val clearedFunds: BigDecimal,
    val postedFunds: BigDecimal,
    val description: String?,
    val subject: String?,
    val notes: String?,
    val location: OperationLocationDto?,
    val tags: List<TagDto>,
    val attachments: List<OperationAttachmentDto>,
    @JsonDeserialize(using = InstantDeserializer::class) val date: Instant,
) {
    constructor(model: OperationModel): this(
        id = model.id,
        import = model.import?.let { ImportDto(it) },
        transferAccount = model.transferAccount?.let { AccountDto(it) },
        account = AccountDto(model.account),
        category = model.category?.let { TypedCategoryDto(it) },
        type = model.type,
        clearedFunds = model.clearedFunds,
        postedFunds = model.postedFunds,
        description = model.description,
        subject = model.subject,
        notes = model.notes,
        location = model.takeIf { it.longitude != null && it.latitude != null }?.let {
            OperationLocationDto(longitude = it.longitude!!, latitude = it.latitude!!)
        },
        tags = model.tags.map { TagDto(it) },
        attachments = model.attachments.map { OperationAttachmentDto(it) },
        date = model.date,
    )
}
