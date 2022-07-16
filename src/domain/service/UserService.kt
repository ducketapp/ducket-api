package dev.ducket.api.domain.service

import dev.ducket.api.app.AccountType
import dev.ducket.api.app.database.Transactional
import dev.ducket.api.domain.mapper.UserMapper
import dev.ducket.api.utils.HashUtils
import dev.ducket.api.domain.controller.account.dto.AccountCreateDto
import dev.ducket.api.domain.controller.user.dto.UserAuthenticateDto
import dev.ducket.api.domain.controller.user.dto.UserCreateDto
import dev.ducket.api.domain.controller.user.dto.UserDto
import dev.ducket.api.domain.controller.user.dto.UserUpdateDto
import dev.ducket.api.domain.repository.*
import dev.ducket.api.plugins.*


class UserService(
    private val userRepository: UserRepository,
    private val accountService: AccountService,
): Transactional {

    suspend fun getUser(userId: Long): UserDto {
        return userRepository.findOne(userId)?.let { UserMapper.mapModelToDto(it) } ?: throw NoDataFoundException()
    }

    suspend fun authenticateUser(dto: UserAuthenticateDto): UserDto {
        return userRepository.findOneByEmail(dto.email)?.let { user ->
            if (HashUtils.check(dto.password, user.passwordHash)) {
                UserMapper.mapModelToDto(user)
            } else {
                throw AuthenticationException("Either password or email is incorrect")
            }
        } ?: throw AuthenticationException()
    }

    suspend fun createUser(dto: UserCreateDto): UserDto {
        userRepository.findOneByEmail(dto.email)?.also {
            throw DuplicateDataException("Such email has already been taken")
        }

        return blockingTransaction {
            userRepository.createOne(UserMapper.mapDtoToModel(dto, HashUtils::hash)).let { user ->
                accountService.createAccount(
                    userId = user.id,
                    dto = AccountCreateDto(
                        title = "Cash ${user.currency.isoCode}",
                        notes = "Account in ${user.currency.name}",
                        startBalance = dto.startBalance,
                        currency = user.currency.isoCode,
                        type = AccountType.CASH,
                    )
                )
                UserMapper.mapModelToDto(user)
            }
        }
    }

    suspend fun updateUser(userId: Long, dto: UserUpdateDto): UserDto {
        return userRepository.updateOne(userId, UserMapper.mapDtoToModel(dto, HashUtils::hash))?.let {
            UserMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException()
    }

    suspend fun deleteUser(userId: Long) {
        blockingTransaction {
            userRepository.deleteData(userId)
            userRepository.deleteOne(userId)
        }
    }

    suspend fun deleteUserData(userId: Long) {
        userRepository.deleteData(userId)
    }
}