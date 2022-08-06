package dev.ducketapp.service.test_data

import dev.ducketapp.service.domain.model.operation.Operation
import dev.ducketapp.service.app.OperationType
import java.math.BigDecimal
import java.time.Instant

class OperationObjectMother {
    companion object {
        fun operation() = Operation(
            id = 1,
            extId = null,
            import = null,
            transferAccount = null,
            account = AccountObjectMother.account(),
            category = CategoryObjectMother.category(),
            user = UserObjectMother.user(),
            type = OperationType.EXPENSE,
            clearedAmount = BigDecimal(10.0),
            postedAmount = BigDecimal(10.0),
            date = Instant.ofEpochSecond(1642708900),
            description = "Operation description",
            subject = "Operation subject",
            notes = "Notes description",
            tags = listOf(
                TagObjectMother.tag()
            ),
            latitude = BigDecimal(50.06167878440914),
            longitude = BigDecimal(19.939386790160416),
            createdAt = Instant.ofEpochSecond(1642708900),
            modifiedAt = Instant.ofEpochSecond(1642708900),
        )
    }
}