package org.expenny.service.domain.controller.operation.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.expenny.service.domain.controller.category.dto.CategoryDto
import org.expenny.service.domain.controller.imports.dto.ImportDto
import org.expenny.service.app.OperationType
import org.expenny.service.domain.controller.account.dto.AccountDto
import org.expenny.service.domain.controller.tag.dto.TagDto
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
    val date: Instant,
)
