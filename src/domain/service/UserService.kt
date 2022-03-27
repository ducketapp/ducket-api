package io.ducket.api.domain.service

import io.ducket.api.app.AccountType
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.controller.user.UserAuthDto
import io.ducket.api.domain.controller.user.UserCreateDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserService(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transferRepository: TransferRepository,
    private val budgetRepository: BudgetRepository,
    private val importRuleRepository: ImportRuleRepository,
    private val importRepository: ImportRepository,
    private val accountService: AccountService,
): FileService() {

    fun getUsers(): List<UserDto> {
        return userRepository.findAll().map { UserDto(it) }
    }

    fun getUser(userId: Long): UserDto {
        return userRepository.findOne(userId)?.let { UserDto(it) } ?: throw NoEntityFoundException()
    }

    fun authenticateUser(reqObj: UserAuthDto): UserDto {
        val foundUser = userRepository.findOneByEmail(reqObj.email) ?: throw AuthenticationException()

        if (BCrypt.checkpw(reqObj.password, foundUser.passwordHash)) return UserDto(foundUser)
        else throw AuthenticationException("The password is incorrect")
    }

    fun setupNewUser(reqObj: UserCreateDto): UserDto {
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
                        accountType = AccountType.CASH,
                    )
                )
                return@transaction UserDto(newUser)
            }
        }
    }

    fun updateUser(userId: Long, reqObj: UserUpdateDto): UserDto {
        return userRepository.updateOne(userId, reqObj)?.let { UserDto(it) } ?: throw NoEntityFoundException()
    }

    fun deleteUser(userId: Long): Boolean {
        return transaction {
            deleteUserData(userId)
            userRepository.deleteOne(userId)
        }
    }

    fun deleteUserData(userId: Long): Boolean {
        return transaction {
            try {
                transactionRepository.deleteAll(userId)
                transferRepository.deleteAll(userId)
                budgetRepository.deleteAll(userId)
                importRepository.deleteAll(userId)
                importRuleRepository.deleteAll(userId)
                accountRepository.deleteAll(userId)
                return@transaction true
            } catch (e: Exception) {
                TransactionManager.current().rollback()
                return@transaction false
            }
        }
    }
}