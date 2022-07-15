package io.ducket.api.domain.mapper

import io.ducket.api.domain.model.account.Account
import io.ducket.api.domain.model.category.Category
import io.ducket.api.domain.model.currency.Currency
import io.ducket.api.domain.model.imports.Import
import io.ducket.api.domain.model.operation.Operation
import io.ducket.api.domain.model.operation.OperationCreate
import io.ducket.api.domain.model.operation.OperationUpdate
import io.ducket.api.app.OperationType
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import io.ducket.api.domain.controller.imports.dto.OperationImportDto
import io.ducket.api.domain.controller.imports.dto.ImportDto
import io.ducket.api.domain.controller.operation.dto.OperationAmountDto
import io.ducket.api.domain.controller.operation.dto.OperationLocationDto
import io.ducket.api.domain.controller.operation.dto.OperationCreateUpdateDto
import io.ducket.api.domain.controller.operation.dto.OperationDto
import io.ducket.api.domain.controller.tag.dto.TagDto
import io.ducket.api.domain.mapper.DataClassMapper.Companion.collectionMapper
import io.ducket.api.domain.model.tag.Tag
import java.math.BigDecimal
import java.util.*

object OperationMapper {

    fun mapDtoToModel(dto: OperationCreateUpdateDto, userId: Long, importId: Long?): OperationCreate {
        return DataClassMapper<OperationCreateUpdateDto, OperationCreate>()
            .provide(OperationCreate::userId, userId)
            .provide(OperationCreate::importId, importId)
            .map("locationData.latitude", OperationCreate::latitude)
            .map("locationData.longitude", OperationCreate::longitude)
            .map("amountData.cleared", OperationCreate::clearedAmount)
            .map("amountData.posted", OperationCreate::postedAmount)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: OperationCreateUpdateDto): OperationUpdate {
        return DataClassMapper<OperationCreateUpdateDto, OperationUpdate>()
            .map("locationData.latitude", OperationCreate::latitude)
            .map("locationData.longitude", OperationCreate::longitude)
            .map("amountData.cleared", OperationCreate::clearedAmount)
            .map("amountData.posted", OperationCreate::postedAmount)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: OperationImportDto, userId: Long, accountId: Long, importId: Long): OperationCreate {
        return DataClassMapper<OperationImportDto, OperationCreate>()
            .provide(OperationCreate::importId, importId)
            .provide(OperationCreate::accountId, accountId)
            .provide(OperationCreate::userId, userId)
            .provide(OperationCreate::date) {
                it.date.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant()
            }
            .provide(OperationCreate::type) {
                if (it.amount < BigDecimal.ZERO) OperationType.EXPENSE else OperationType.INCOME
            }
            .map("amount", OperationCreate::clearedAmount)
            .map("amount", OperationCreate::postedAmount)
            .invoke(dto)
    }

    fun mapModelToDto(model: Operation): OperationDto {
        val tagsMapper = DataClassMapper<Tag, TagDto>()
        val categoryMapper = DataClassMapper<Category, CategoryDto>()
        val importMapper = DataClassMapper<Import, ImportDto>()

        val accountMapper = DataClassMapper<Account, AccountDto>()
            .register("currency", DataClassMapper<Currency, CurrencyDto>())

        return DataClassMapper<Operation, OperationDto>()
            .register(OperationDto::tags, collectionMapper(tagsMapper))
            .register(OperationDto::account, accountMapper)
            .register(OperationDto::transferAccount, accountMapper)
            .register(OperationDto::import, importMapper)
            .register(OperationDto::category, categoryMapper)
            .provide(OperationDto::amount) { source ->
                OperationAmountDto(source.postedAmount, source.clearedAmount)
            }
            .provide(OperationDto::location) { source ->
                source.takeIf { it.longitude != null && it.latitude != null }?.let {
                    OperationLocationDto(it.longitude!!, it.latitude!!)
                }
            }.invoke(model)
    }
}