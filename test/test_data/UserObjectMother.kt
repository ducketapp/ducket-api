package dev.ducket.api.test_data

import dev.ducket.api.domain.model.user.User
import dev.ducket.api.domain.model.user.UserCreate
import dev.ducket.api.domain.model.user.UserUpdate
import dev.ducket.api.domain.controller.user.dto.UserAuthenticateDto
import dev.ducket.api.domain.controller.user.dto.UserCreateDto
import dev.ducket.api.domain.controller.user.dto.UserDto
import dev.ducket.api.domain.controller.user.dto.UserUpdateDto
import dev.ducket.api.utils.toLocalDate
import java.math.BigDecimal
import java.time.Instant

class UserObjectMother {
    companion object {
        fun user(): User = User(
            id = 1L,
            name = "John Wick",
            phone = "+48123456789",
            email = "johnwick@test.com",
            passwordHash = "hash",
            currency = CurrencyObjectMother.currency(),
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun userCreate(): UserCreate = UserCreate(
            name = "John Wick",
            phone = "+48123456789",
            email = "johnwick@test.com",
            passwordHash = "hash",
            currency = CurrencyObjectMother.currency().isoCode
        )

        fun userUpdate(): UserUpdate = UserUpdate(
            name = "John Quick",
            phone = "+48987654321",
            passwordHash = "hash",
        )

        fun userCreateDto(): UserCreateDto = UserCreateDto(
            name = "John Wick",
            phone = "+48123456789",
            email = "johnwick@test.com",
            password = "1234",
            currency = CurrencyObjectMother.currency().isoCode,
            defaultAccount = null
        )

        fun userAuthDto(): UserAuthenticateDto = UserAuthenticateDto(
            email = "johnwick@test.com",
            password = "1234",
        )

        fun userUpdateDto(): UserUpdateDto = UserUpdateDto(
            name = "John Quick",
            phone = "+48987654321",
            password = "4321",
        )

        fun userDto(): UserDto = UserDto(
            id = 1L,
            name = "John Wick",
            phone = "+48123456789",
            email = "johnwick@test.com",
            currency = CurrencyObjectMother.currencyDto(),
            sinceDate = Instant.ofEpochSecond(1642708900).toLocalDate(),
        )
    }
}