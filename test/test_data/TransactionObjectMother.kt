package io.ducket.api.test_data

import domain.model.operation.Transaction
import io.ducket.api.app.DefaultCategory
import io.ducket.api.app.DefaultCategoryGroup
import io.ducket.api.domain.controller.transaction.TransactionCreateDto
import java.math.BigDecimal
import java.time.Instant

class TransactionObjectMother {
    companion object {
        fun transaction() = Transaction(
            id = 1L,
            account = AccountObjectMother.account(),
            category = CategoryObjectMother.category(),
            user = UserObjectMother.user(),
            import = null,
            amount = BigDecimal(10.0),
            date = Instant.ofEpochSecond(1642708900),
            payeeOrPayer = "",
            notes = "",
            longitude = null,
            latitude = null,
            attachments = listOf(),
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )

        fun newCorrectiveTransaction(amount: BigDecimal) = TransactionCreateDto(
            amount = amount,
            accountId = AccountObjectMother.account().id,
            category = DefaultCategory.OTHER,
            categoryGroup = DefaultCategoryGroup.OTHER,
            notes = "Corrective transaction",
            date = Instant.ofEpochSecond(1642708900),
        )
    }
}