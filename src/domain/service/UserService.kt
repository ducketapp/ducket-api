package io.ducket.api.domain.service

import io.ducket.api.app.AccountType
import io.ducket.api.app.database.Transactional
import io.ducket.api.utils.HashUtils
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.user.*
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.*


class UserService(
    private val userRepository: UserRepository,
    private val accountService: AccountService,
): Transactional {

    suspend fun getUser(userId: Long): UserDto {
        return userRepository.findOne(userId)?.let { UserDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun authenticateUser(reqObj: UserAuthenticateDto): UserDto {
        val user = userRepository.findOneByEmail(reqObj.email)

        if (user != null && HashUtils.check(reqObj.password, user.passwordHash)) return UserDto(user)
        else throw AuthenticationException("Either password or email is incorrect")
    }

    suspend fun createUser(reqObj: UserCreateDto): UserDto {
        userRepository.findOneByEmail(reqObj.email)?.also {
            throw AuthenticationException("Such email has already been taken")
        }

        return blockingTransaction {
            userRepository.createOne(reqObj).let { user ->
                accountService.createAccount(
                    userId = user.id,
                    reqObj = AccountCreateDto(
                        name = "Cash ${user.mainCurrency.isoCode}",
                        notes = "Account in ${user.mainCurrency.name}",
                        startBalance = reqObj.startBalance,
                        currencyIsoCode = user.mainCurrency.isoCode,
                        type = AccountType.CASH,
                    )
                )
                return@blockingTransaction UserDto(user)
            }
        }
    }

    suspend fun updateUser(userId: Long, reqObj: UserUpdateDto): UserDto {
        return userRepository.updateOne(userId, reqObj)?.let { UserDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun deleteUser(userId: Long) {
        return blockingTransaction {
            userRepository.deleteData(userId)
            userRepository.deleteOne(userId)
        }
    }

    suspend fun deleteUserData(userId: Long) {
        return userRepository.deleteData(userId)
    }
}