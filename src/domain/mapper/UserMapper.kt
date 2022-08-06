package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.model.user.User
import dev.ducketapp.service.domain.model.user.UserCreate
import dev.ducketapp.service.domain.model.user.UserUpdate
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.controller.user.dto.UserCreateDto
import dev.ducketapp.service.domain.controller.user.dto.UserDto
import dev.ducketapp.service.domain.controller.user.dto.UserUpdateDto
import dev.ducketapp.service.utils.toLocalDate

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