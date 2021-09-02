package io.budgery.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.account.AccountType

class AccountTypeDto(@JsonUnwrapped val accountType: AccountType)