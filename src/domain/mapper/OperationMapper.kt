package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.account.Account
import dev.ducketapp.service.domain.model.category.Category
import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.model.imports.Import
import dev.ducketapp.service.domain.model.operation.Operation
import dev.ducketapp.service.domain.model.operation.OperationCreate
import dev.ducketapp.service.domain.model.operation.OperationUpdate
import dev.ducketapp.service.app.OperationType
import dev.ducketapp.service.domain.controller.account.dto.AccountDto
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.controller.imports.dto.OperationImportDto
import dev.ducketapp.service.domain.controller.imports.dto.ImportDto
import dev.ducketapp.service.domain.controller.operation.dto.OperationAmountDto
import dev.ducketapp.service.domain.controller.operation.dto.OperationLocationDto
import dev.ducketapp.service.domain.controller.operation.dto.OperationCreateUpdateDto
import dev.ducketapp.service.domain.controller.operation.dto.OperationDto
import dev.ducketapp.service.domain.controller.tag.dto.TagDto
import dev.ducketapp.service.domain.mapper.DataClassMapper.Companion.collectionMapper
import dev.ducketapp.service.domain.model.tag.Tag
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