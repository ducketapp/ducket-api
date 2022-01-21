package io.ducket.api.app

enum class UserFollowAction {
    APPROVE, DELETE
}

enum class BudgetPeriodType {
    WEEKLY, MONTHLY, ANNUAL
}

enum class AccountType {
    GENERAL, DEBIT_CARD, CREDIT_CARD, CASH, BANK_ACCOUNT, SAVINGS
}

enum class CategoryGroup {
    FOOD_AND_DRINKS,
    HOUSING,
    SHOPPING,
    TRANSPORT,
    UTILITIES,
    INVESTMENTS,
    PETS,
    INFLOW,
    CHILDREN,
    FINANCIAL_COSTS,
    LIFE_AND_LEISURE,
    UNCATEGORIZED,
}