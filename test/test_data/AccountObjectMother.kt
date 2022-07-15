package io.ducket.api.test_data

import io.ducket.api.domain.model.account.Account
import io.ducket.api.app.AccountType
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.account.dto.AccountUpdateDto
import io.ducket.api.domain.model.account.AccountCreate
import io.ducket.api.domain.model.account.AccountUpdate
import java.math.BigDecimal
import java.time.Instant

class AccountObjectMother {
    companion object {
        fun account() = Account(
            id = 1L,
            extId = null,
            title = "Cash USD",
            notes = "Account in United States dollar",
            startBalance = BigDecimal.ZERO,
            totalBalance = BigDecimal.ZERO,
            user = UserObjectMother.user(),
            currency = CurrencyObjectMother.currency(),
            type = AccountType.CASH,
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun accountCreate() = AccountCreate(
            extId = null,
            userId = UserObjectMother.user().id,
            title = "Cash USD",
            notes = "Account in United States dollar",
            currency = "USD",
            startBalance = BigDecimal.ZERO,
            type = AccountType.CASH
        )

        fun accountUpdate() = AccountUpdate(
            title = "Savings USD",
            notes = "Savings in United States dollar",
            startBalance = BigDecimal(12_345_678.90),
            type = AccountType.SAVINGS
        )

        fun accountCreateDto() = AccountCreateDto(
            title = "Cash USD",
            notes = "Account in United States dollar",
            currency = "USD",
            startBalance = BigDecimal.ZERO,
            type = AccountType.CASH,
        )

        fun accountUpdateDto() = AccountUpdateDto(
            title = "Savings USD",
            notes = "Savings in United States dollar",
            startBalance = BigDecimal(12_345_678.90),
            type = AccountType.SAVINGS
        )

        fun accountDto() = AccountDto(
            id = 1L,
            extId = null,
            title = "Cash USD",
            startBalance = BigDecimal.ZERO,
            totalBalance = BigDecimal.ZERO,
            type = AccountType.CASH,
            notes = "Account in United States dollar",
            currency = CurrencyObjectMother.currencyDto()
        )
    }
}