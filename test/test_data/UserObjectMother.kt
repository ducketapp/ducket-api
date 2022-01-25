package io.ducket.api.test_data

import domain.model.user.User
import io.ducket.api.domain.controller.user.UserAuthDto
import io.ducket.api.domain.controller.user.UserCreateDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

class UserObjectMother {
    companion object {
        fun default(): User = User(
            id = 1,
            name = "",
            phone = "",
            email = "",
            mainCurrency = CurrencyObjectMother.default(),
            passwordHash = "",
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun johnWick(): User = User(
            id = 2,
            name = "John Wick",
            phone = "+12025550115",
            email = "johnwick@test.com",
            mainCurrency = CurrencyObjectMother.usd(),
            passwordHash = "hash",
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun newJohnWick(): UserCreateDto = UserCreateDto(
            name = "John Wick",
            phone = "+12025550115",
            email = "johnwick@test.com",
            password = "1234",
            currencyIsoCode = "USD",
        )

        fun authJohnWick(): UserAuthDto = UserAuthDto(
            email = "johnwick@test.com",
            password = "1234",
        )

        fun updateJohnWickAll(): UserUpdateDto = UserUpdateDto(
            name = "John Quick",
            phone = "+15236112350",
            password = "4321",
        )

        fun updateJohnWick(): UserUpdateDto = UserUpdateDto(
            name = "John Wick",
            phone = "+12025550115",
            password = "1234",
        )
    }
}