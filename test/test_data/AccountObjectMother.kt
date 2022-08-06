package dev.ducketapp.service.test_data

import dev.ducketapp.service.domain.model.account.Account
import dev.ducketapp.service.app.AccountType
import dev.ducketapp.service.domain.controller.account.dto.AccountCreateDto
import dev.ducketapp.service.domain.controller.account.dto.AccountDto
import dev.ducketapp.service.domain.controller.account.dto.AccountUpdateDto
import dev.ducketapp.service.domain.model.account.AccountCreate
import dev.ducketapp.service.domain.model.account.AccountUpdate
import java.math.BigDecimal
import java.time.Instant

class AccountObjectMother {
    companion object {
        fun account() = Account(
            id = 1L,
            extId = null,
            name = "Cash USD",
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
            name = "Cash USD",
            notes = "Account in United States dollar",
            currency = "USD",
            startBalance = BigDecimal.ZERO,
            type = AccountType.CASH
        )

        fun accountUpdate() = AccountUpdate(
            name = "Savings USD",
            notes = "Savings in United States dollar",
            startBalance = BigDecimal(12_345_678.90),
            type = AccountType.SAVINGS
        )

        fun accountCreateDto() = AccountCreateDto(
            name = "Cash USD",
            notes = "Account in United States dollar",
            currency = "USD",
            startBalance = BigDecimal.ZERO,
            type = AccountType.CASH,
        )

        fun accountUpdateDto() = AccountUpdateDto(
            name = "Savings USD",
            notes = "Savings in United States dollar",
            startBalance = BigDecimal(12_345_678.90),
            type = AccountType.SAVINGS
        )

        fun accountDto() = AccountDto(
            id = 1L,
            extId = null,
            name = "Cash USD",
            startBalance = BigDecimal.ZERO,
            totalBalance = BigDecimal.ZERO,
            type = AccountType.CASH,
            notes = "Account in United States dollar",
            currency = CurrencyObjectMother.currencyDto()
        )
    }
}