package io.ducket.api.domain.controller.ledger

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.utils.InstantSerializer
import io.ducket.api.domain.controller.category.TypedCategoryDto
import io.ducket.api.domain.controller.imports.ImportDto
import io.ducket.api.domain.controller.user.UserDto
import domain.model.operation.Operation
import java.math.BigDecimal
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OperationDto(
    val id: Long,
    val user: UserDto,
    val category: TypedCategoryDto,
    val import: ImportDto?,
    val description: String?,
    val subject: String?,
    val notes: String?,
    val attachments: List<OperationAttachmentDto>?,
    val longitude: BigDecimal?,
    val latitude: BigDecimal?,
    @JsonSerialize(using = InstantSerializer::class) val date: Instant,
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant,
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant,
) {
    constructor(operation: Operation): this(
        id = operation.id,
        user = UserDto(operation.user),
        category = TypedCategoryDto(operation.category),
        import = operation.import?.let { ImportDto(it) },
        description = operation.description,
        subject = operation.subject,
        notes = operation.notes,
        attachments = operation.attachments.map { OperationAttachmentDto(it) },
        longitude = operation.longitude,
        latitude = operation.latitude,
        date = operation.date,
        createdAt = operation.createdAt,
        modifiedAt = operation.modifiedAt,
    )
}
