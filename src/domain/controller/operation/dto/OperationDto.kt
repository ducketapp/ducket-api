package io.ducket.api.domain.controller.operation.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.imports.dto.ImportDto
import io.ducket.api.app.OperationType
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.tag.dto.TagDto
import io.ducket.api.utils.InstantSerializer
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OperationDto(
    val id: Long,
    val extId: String?,
    val import: ImportDto?,
    val transferAccount: AccountDto?,
    val account: AccountDto,
    val category: CategoryDto?,
    val type: OperationType,
    val amount: OperationAmountDto,
    val description: String?,
    val subject: String?,
    val notes: String?,
    val location: OperationLocationDto?,
    val tags: List<TagDto>,
    @JsonSerialize(using = InstantSerializer::class) val date: Instant,
)
