package io.budgery.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonUnwrapped

class AccountsResponse(@JsonUnwrapped val account: AccountDto)