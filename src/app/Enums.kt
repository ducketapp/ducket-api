package io.ducket.api.app

enum class AccountType {
    GENERAL, DEBIT_CARD, CREDIT_CARD, CASH, BANK_ACCOUNT, SAVINGS
}

enum class BudgetPeriodType {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY,
}

enum class LedgerRecordStrategy {
    RECORD, TRANSFER
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
