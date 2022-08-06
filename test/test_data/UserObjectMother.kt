package dev.ducketapp.service.test_data

import dev.ducketapp.service.domain.model.user.User
import dev.ducketapp.service.domain.model.user.UserCreate
import dev.ducketapp.service.domain.model.user.UserUpdate
import dev.ducketapp.service.domain.controller.user.dto.UserAuthenticateDto
import dev.ducketapp.service.domain.controller.user.dto.UserCreateDto
import dev.ducketapp.service.domain.controller.user.dto.UserDto
import dev.ducketapp.service.domain.controller.user.dto.UserUpdateDto
import dev.ducketapp.service.utils.toLocalDate
import java.math.BigDecimal
import java.time.Instant

class UserObjectMother {
    companion object {
        fun user(): User = User(
            id = 1L,
            name = "John Wick",
            email = "johnwick@test.com",
            passwordHash = "hash",
            currency = CurrencyObjectMother.currency(),
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun userCreate(): UserCreate = UserCreate(
            name = "John Wick",
            email = "johnwick@test.com",
            passwordHash = "hash",
            currency = CurrencyObjectMother.currency().isoCode
        )

        fun userUpdate(): UserUpdate = UserUpdate(
            name = "John Quick",
            passwordHash = "hash",
        )

        fun userCreateDto(): UserCreateDto = UserCreateDto(
            name = "John Wick",
            email = "johnwick@test.com",
            password = "1234",
            currency = CurrencyObjectMother.currency().isoCode,
        )

        fun userAuthDto(): UserAuthenticateDto = UserAuthenticateDto(
            email = "johnwick@test.com",
            password = "1234",
        )

        fun userUpdateDto(): UserUpdateDto = UserUpdateDto(
            name = "John Quick",
            password = "4321",
        )

        fun userDto(): UserDto = UserDto(
            id = 1L,
            name = "John Wick",
            email = "johnwick@test.com",
            currency = CurrencyObjectMother.currencyDto(),
            createdAt = Instant.ofEpochSecond(1642708900).toEpochMilli(),
            modifiedAt = Instant.ofEpochSecond(1642708900).toEpochMilli(),
        )
    }
}