package io.ducket.api.app

import org.threeten.extra.LocalDateRange

enum class AccountType {
    GENERAL, DEBIT_CARD, CREDIT_CARD, CASH, BANK_ACCOUNT, SAVINGS
}

enum class PeriodicBudgetType {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY,
}

enum class PeriodicBudgetLimitUpdateStrategy {
    CURRENT, CURRENT_FUTURE, CURRENT_PAST, ALL
}

enum class OperationType {
    INCOME, EXPENSE, TRANSFER
}

enum class Permission {
    READ_ONLY, NOT_PERMITTED
}

enum class AccountPermission {
    TRACK_AND_READ, READ_ONLY, NOT_PERMITTED
}

enum class ImportRuleLookupType {
    INCOME_ONLY, EXPENSE_ONLY, ANY
}
