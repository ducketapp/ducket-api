package dev.ducketapp.service.app


@Suppress("unused")
enum class AccountType {
    GENERAL, DEBIT_CARD, CREDIT_CARD, CASH, BANK_ACCOUNT, SAVINGS
}

enum class PeriodicBudgetType {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY,
}

enum class OperationType {
    INCOME, EXPENSE, TRANSFER
}

@Suppress("unused")
enum class AccountPermission {
    TRACK_AND_READ, READ_ONLY, NOT_PERMITTED
}

@Suppress("unused")
enum class ImportRuleApplyType {
    INCOME_ONLY, EXPENSE_ONLY, ANY
}
