package domain.mapper

import domain.model.currency.Currency
import domain.model.user.User
import domain.model.user.UserCreate
import domain.model.user.UserEntity
import domain.model.user.UserUpdate
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import io.ducket.api.domain.controller.user.dto.UserCreateDto
import io.ducket.api.domain.controller.user.dto.UserDto
import io.ducket.api.domain.controller.user.dto.UserUpdateDto
import io.ducket.api.utils.toLocalDate

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
            .register(UserDto::currency, DataClassMapper<Currency, CurrencyDto>())
            .provide(UserDto::sinceDate) { it.createdAt.toLocalDate() }
            .invoke(model)
    }

    fun mapEntityToModel(entity: UserEntity): User {
        return DataClassMapper<UserEntity, User>()
            .map("id.value", User::id)
            .provide(User::currency, entity.currency.toModel())
            .invoke(entity)
    }
}