package io.ducket.api.app

enum class UserFollowAction {
    APPROVE, DELETE
}

enum class AccountType {
    GENERAL, DEBIT_CARD, CREDIT_CARD, CASH, BANK_ACCOUNT, SAVINGS
}

enum class MembershipAction {
    ACCEPT, CANCEL
}

enum class MembershipStatus {
    PENDING, ACTIVE
}

enum class CategoryGroup {
    HOUSING,
    FOOD_AND_DRINKS,
    SHOPPING,
    FAMILY,
    LEISURE,
    PERSONAL_CARE,
    TRANSPORT,
    FINANCIAL_COSTS,
    INVESTMENTS,
    INCOME,
    OTHER,
}