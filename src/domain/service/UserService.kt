package io.ducket.api.domain.service

import io.ducket.api.app.AccountType
import io.ducket.api.utils.HashUtils
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.user.*
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserService(
    private val userRepository: UserRepository,
    private val accountService: AccountService,
    private val groupRepository: GroupRepository,
): LocalFileService() {

    fun getUser(userId: Long): UserDto {
        return userRepository.findOne(userId)?.let { UserDto(it) } ?: throw NoEntityFoundException()
    }

    fun authenticateUser(reqObj: UserAuthenticateDto): UserDto {
        val foundUser = userRepository.findOneByEmail(reqObj.email) ?: throw AuthenticationException()

        if (HashUtils.check(reqObj.password, foundUser.passwordHash)) return UserDto(foundUser)
        else throw AuthenticationException("The password is incorrect")
    }

    fun createUser(reqObj: UserCreateDto): UserDto {
        userRepository.findOneByEmail(reqObj.email)?.let {
            throw DuplicateEntityException("Such email has already been taken")
        }

        return transaction {
            userRepository.create(reqObj).let { newUser ->
                accountService.createAccount(
                    userId = newUser.id,
                    payload = AccountCreateDto(
                        name = "Cash ${newUser.mainCurrency.isoCode}",
                        notes = "Account in ${newUser.mainCurrency.name}",
                        startBalance = reqObj.startBalance,
                        currencyIsoCode = newUser.mainCurrency.isoCode,
                        type = AccountType.CASH,
                    )
                )
                return@transaction UserDto(newUser)
            }
        }
    }

    fun updateUser(userId: Long, reqObj: UserUpdateDto): UserDto {
        return userRepository.updateOne(userId, reqObj)?.let { UserDto(it) } ?: throw NoEntityFoundException()
    }

    fun deleteUser(userId: Long) {
        return transaction {
            userRepository.deleteData(userId)
            userRepository.deleteOne(userId)
        }
    }

    fun deleteUserData(userId: Long) {
        return userRepository.deleteData(userId)
    }
}