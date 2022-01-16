package io.ducket.api.domain.service

import domain.model.account.AccountType
import io.ducket.api.*
import io.ducket.api.domain.controller.account.AccountCreateDto
import io.ducket.api.domain.controller.follow.FollowDto
import io.ducket.api.domain.controller.follow.FollowerDto
import io.ducket.api.domain.controller.follow.FollowedDto
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.controller.user.UserSignInDto
import io.ducket.api.domain.controller.user.UserSignUpDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import io.ducket.api.domain.repository.AccountRepository
import io.ducket.api.domain.repository.FollowRepository
import io.ducket.api.domain.repository.UserRepository
import io.ducket.api.plugins.AuthenticationException
import io.ducket.api.plugins.DuplicateEntityError
import io.ducket.api.plugins.InvalidDataError
import io.ducket.api.plugins.NoEntityFoundError
import org.mindrot.jbcrypt.BCrypt

class UserService(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val followRepository: FollowRepository,
): FileService() {
    private val logger = getLogger()

    fun getUsers(): List<UserDto> {
        return userRepository.findAll().map { UserDto(it) }
    }

    fun getUser(userId: Long): UserDto {
        return userRepository.findOne(userId)?.let { UserDto(it) } ?: throw NoEntityFoundError("No such user was found")
    }

    fun getUser(reqObj: UserSignInDto): UserDto {
        val foundUser = userRepository.findOneByEmail(reqObj.email) ?: throw AuthenticationException("The user doesn't exist")

        return UserDto(foundUser).takeIf {
            BCrypt.checkpw(reqObj.password, foundUser.passwordHash)
        } ?: throw AuthenticationException("The password is incorrect")
    }

    fun createUser(reqObj: UserSignUpDto): UserDto {
        userRepository.findOneByEmail(reqObj.email)?.let {
            throw DuplicateEntityError("Such email has already been taken")
        }

        userRepository.create(reqObj).also { newUser ->
            try {
                accountRepository.create(newUser.id,
                    AccountCreateDto(
                        name = "Wallet",
                        notes = "Account in ${newUser.mainCurrency.name}",
                        currencyId = newUser.mainCurrency.id,
                        accountType = AccountType.CASH
                    )
                )
            } catch (e: Exception) {
                logger.error("Cannot create default user account", e)
            }

            return UserDto(newUser)
        }
    }

    fun updateUser(userId: Long, reqObj: UserUpdateDto): UserDto {
        return userRepository.updateOne(userId, reqObj)?.let { UserDto(it) }
            ?: throw Exception("Cannot update user entity")
    }

    fun deleteUser(userId: Long): Boolean {
        return userRepository.deleteOne(userId)
    }

    fun deleteUserData(userId: Long): Boolean {
        return userRepository.deleteUserData(userId)
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
        val follow = followRepository.findOne(followRequestId) ?: throw NoEntityFoundError("No such follow was found")

        when (action.toLowerCase()) {
            "approve" -> {
                if (follow.followed.id == userId) {
                    followRepository.approveFollow(userId, follow.id)?.let { FollowerDto(it) }
                } else {
                    throw Exception("Cannot approve follow request")
                }
            }
            "delete" -> {
                if (userId in listOf(follow.followed.id, follow.follower.id)) {
                    followRepository.deleteFollow(userId, follow.id)
                } else {
                    throw Exception("Cannot delete follow request")
                }
            }
            else -> throw InvalidDataError("Unrecognized action")
        } ?: throw Exception("Cannot apply an action")

        return followRepository.findFollowsByUser(userId).map { FollowDto(it) }
    }
}