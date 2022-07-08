package io.ducket.api.domain.service

import io.ducket.api.app.AccountType
import io.ducket.api.app.database.Transactional
import domain.mapper.UserMapper
import io.ducket.api.utils.HashUtils
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.user.dto.UserAuthenticateDto
import io.ducket.api.domain.controller.user.dto.UserCreateDto
import io.ducket.api.domain.controller.user.dto.UserDto
import io.ducket.api.domain.controller.user.dto.UserUpdateDto
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.*


class UserService(
    private val userRepository: UserRepository,
    private val accountService: AccountService,
): Transactional {

    suspend fun getUser(userId: Long): UserDto {
        return userRepository.findOne(userId)?.let { UserMapper.mapModelToDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun authenticateUser(dto: UserAuthenticateDto): UserDto {
        return userRepository.findOneByEmail(dto.email).let { user ->
            if (user != null && HashUtils.check(dto.password, user.passwordHash)) {
                UserMapper.mapModelToDto(user)
            } else {
                throw AuthenticationException("Either password or email is incorrect")
            }
        }
    }

    suspend fun createUser(dto: UserCreateDto): UserDto {
        userRepository.findOneByEmail(dto.email)?.also {
            throw AuthenticationException("Such email has already been taken")
        }

        return blockingTransaction {
            userRepository.createOne(UserMapper.mapDtoToModel(dto, HashUtils::hash)).let { user ->
                accountService.createAccount(
                    userId = user.id,
                    dto = AccountCreateDto(
                        name = "Cash ${user.currency.isoCode}",
                        notes = "Account in ${user.currency.name}",
                        startBalance = dto.startBalance,
                        currency = user.currency.isoCode,
                        type = AccountType.CASH,
                    )
                )
                return@blockingTransaction UserMapper.mapModelToDto(user)
            }
        }
    }

    suspend fun updateUser(userId: Long, dto: UserUpdateDto): UserDto {
        return userRepository.updateOne(userId, UserMapper.mapDtoToModel(dto, HashUtils::hash))?.let {
            UserMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
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