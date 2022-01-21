package io.ducket.api.domain.service

import io.ducket.api.*
import io.ducket.api.app.AccountType
import io.ducket.api.app.UserFollowAction
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.follow.FollowDto
import io.ducket.api.domain.controller.follow.FollowerDto
import io.ducket.api.domain.controller.follow.FollowedDto
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
    private val followRepository: FollowRepository,
    private val transactionRepository: TransactionRepository,
    private val transferRepository: TransferRepository,
    private val budgetRepository: BudgetRepository,
    private val importRuleRepository: ImportRuleRepository,
    private val importRepository: ImportRepository,
): FileService() {

    fun getUsers(): List<UserDto> {
        return userRepository.findAll().map { UserDto(it) }
    }

    fun getUser(userId: Long): UserDto {
        return userRepository.findOne(userId)?.let { UserDto(it) } ?: throw NoEntityFoundException("No such user was found")
    }

    fun authenticateUser(reqObj: UserAuthDto): UserDto {
        val foundUser = userRepository.findOneByEmail(reqObj.email) ?: throw AuthenticationException("The user doesn't exist")

        if (BCrypt.checkpw(reqObj.password, foundUser.passwordHash)) return UserDto(foundUser)
        else throw AuthenticationException("The password is incorrect")
    }

    fun setupNewUser(reqObj: UserCreateDto): UserDto {
        userRepository.findOneByEmail(reqObj.email)?.let {
            throw DuplicateEntityException("Such email has already been taken")
        }

        return transaction {
            userRepository.create(reqObj).let { newUser ->
                accountRepository.create(newUser.id,
                    AccountCreateDto(
                        name = "Cash ${newUser.mainCurrency.isoCode}",
                        notes = "Account in ${newUser.mainCurrency.name}",
                        currencyIsoCode = newUser.mainCurrency.isoCode,
                        accountType = AccountType.CASH,
                    )
                )
                return@transaction UserDto(newUser)
            }
        }
    }

    fun updateUser(userId: Long, reqObj: UserUpdateDto): UserDto {
        return userRepository.updateOne(userId, reqObj)?.let { UserDto(it) }
            ?: throw Exception("Cannot update user entity")
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

    fun createUserFollowRequest(userId: Long, userToFollowId: Long) : FollowedDto {
        return FollowedDto(followRepository.createRequest(userId, userToFollowId))
    }

    fun getUserFollowing(userId: Long) : List<FollowDto> {
        return followRepository.findFollowingByUser(userId).map { FollowDto(it) }
    }

    fun getUserFollowers(userId: Long) : List<FollowDto> {
        return followRepository.findFollowersByUser(userId).map { FollowDto(it) }
    }

    fun updateUserFollow(userId: Long, followRequestId: Long, action: String): List<FollowDto> {
        val follow = followRepository.findOne(followRequestId) ?: throw NoEntityFoundException("No such follow was found")

        when (UserFollowAction.valueOf(action)) {
            UserFollowAction.APPROVE -> {
                if (follow.followed.id == userId) {
                    followRepository.approveFollow(userId, follow.id)?.let { FollowerDto(it) }
                } else {
                    throw BusinessException("Cannot approve a follow request which doesn't belong to the user")
                }
            }
            UserFollowAction.DELETE -> {
                if (userId in listOf(follow.followed.id, follow.follower.id)) {
                    followRepository.deleteFollow(userId, follow.id)
                } else {
                    throw BusinessException("Cannot delete a follow which doesn't belong to the user")
                }
            }
        } ?: throw Exception("Cannot apply an action to the user's follow: $action")

        return followRepository.findFollowsByUser(userId).map { FollowDto(it) }
    }
}