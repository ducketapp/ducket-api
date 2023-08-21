package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.model.user.User
import org.expenny.service.domain.model.user.UserCreate
import org.expenny.service.domain.model.user.UserUpdate
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import org.expenny.service.domain.controller.user.dto.UserCreateDto
import org.expenny.service.domain.controller.user.dto.UserDto
import org.expenny.service.domain.controller.user.dto.UserUpdateDto
import org.expenny.service.utils.toLocalDate

object UserMapper {

    fun mapDtoToModel(dto: UserCreateDto, hashPassword: (password: String) -> String): UserCreate {
        return DataClassMapper<UserCreateDto, UserCreate>()
            .provide(UserCreate::passwordHash) { hashPassword(it.password) }
            .invoke(dto)
    }

    fun mapDtoToModel(dto: UserUpdateDto, hashPassword: (password: String) -> String): UserUpdate {
        return DataClassMapper<UserUpdateDto, UserUpdate>()
            .provide(UserCreate::passwordHash) { hashPassword(it.password) }
            .invoke(dto)
    }

    fun mapModelToDto(model: User): UserDto {
        return DataClassMapper<User, UserDto>()
            .provide(UserDto::modifiedAt) { it.modifiedAt.toEpochMilli() }
            .provide(UserDto::createdAt) { it.createdAt.toEpochMilli() }
            .register(UserDto::currency, DataClassMapper<Currency, CurrencyDto>())
            .invoke(model)
    }
}