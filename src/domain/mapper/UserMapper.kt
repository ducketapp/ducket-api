package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.currency.Currency
import dev.ducket.api.domain.model.user.User
import dev.ducket.api.domain.model.user.UserCreate
import dev.ducket.api.domain.model.user.UserUpdate
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import dev.ducket.api.domain.controller.user.dto.UserCreateDto
import dev.ducket.api.domain.controller.user.dto.UserDto
import dev.ducket.api.domain.controller.user.dto.UserUpdateDto
import dev.ducket.api.utils.toLocalDate

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