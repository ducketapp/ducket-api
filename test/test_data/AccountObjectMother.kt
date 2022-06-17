package io.ducket.api.test_data

import domain.model.account.Account
import io.ducket.api.app.AccountType
import io.ducket.api.domain.controller.account.AccountCreateDto
import java.math.BigDecimal
import java.time.Instant

class AccountObjectMother {
    companion object {
        fun default() = Account(
            id = 1,
            name = "",
            notes = "",
            user = UserObjectMother.default(),
            currency = CurrencyObjectMother.default(),
            type = AccountType.CASH,
            recordsCount = 0,
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun cashEur() = Account(
            id = 2,
            name = "Cash EUR",
            notes = "Account in Euro",
            user = UserObjectMother.default(),
            currency = CurrencyObjectMother.default(),
            type = AccountType.CASH,
            recordsCount = 0,
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun account() = Account(
            id = 3L,
            name = "Cash USD",
            notes = "Account in United States dollar",
            user = UserObjectMother.default(),
            currency = CurrencyObjectMother.default(),
            type = AccountType.CASH,
            recordsCount = 0,
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun newAccount() = AccountCreateDto(
            name = "Cash USD",
            notes = "Account in United States dollar",
            currencyIsoCode = "USD",
            startBalance = BigDecimal.ZERO,
            type = AccountType.CASH,
        )
    }
}