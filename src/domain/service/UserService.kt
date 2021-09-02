package io.budgery.api.domain.service

import io.budgery.api.AuthenticationException
import io.budgery.api.domain.controller.account.AccountCreateDto
import io.budgery.api.domain.controller.user.UserDto
import io.budgery.api.domain.controller.user.UserSignInDto
import io.budgery.api.domain.controller.user.UserSignUpDto
import io.budgery.api.domain.controller.user.UserUpdateDto
import io.budgery.api.domain.repository.AccountRepository
import io.budgery.api.domain.repository.UserRepository
import io.ktor.http.content.*
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.util.*
import kotlin.NoSuchElementException

class UserService(private val userRepository: UserRepository, private val accountRepository: AccountRepository) {

    fun getUser(userId: Int): UserDto {
        userRepository.findById(userId)?.let {
            return UserDto(it)
        } ?: throw NoSuchElementException("No such user was found")
    }

    fun createUser(reqObj: UserSignUpDto): UserDto {
        userRepository.findByEmail(reqObj.email)?.let {
            throw IllegalArgumentException("The user with such email already exists")
        }

        val newUser = userRepository.create(reqObj)

        // create default account for new user
        accountRepository.findTypeByName("Cash")?.let {
            accountRepository.create(newUser.id,
                AccountCreateDto(
                    name = it.name,
                    notes = "Account in ${newUser.mainCurrency.name}",
                    currencyId = newUser.mainCurrency.id,
                    accountTypeId = it.id
                )
            )
        }

        return UserDto(newUser)
    }

    fun authenticateUser(reqObj: UserSignInDto): UserDto {
        userRepository.findByEmail(reqObj.email)?.let {
            if (BCrypt.checkpw(reqObj.password, it.passwordHash)) {
                return UserDto(it)
            } else {
                throw AuthenticationException("The entered password is incorrect")
            }
        } ?: throw IllegalArgumentException("The user with such email does not exist")
    }

    fun updateUser(userId: Int, reqObj: UserUpdateDto): UserDto {
        userRepository.updateById(userId, reqObj)?.let {
            return UserDto(it)
        } ?: throw NoSuchElementException("Cannot update. No such user was found")
    }

    suspend fun uploadImage(userId: Int, multipart: MultiPartData): File {
        var imageFile = File("uploads")
        imageFile.mkdirs()

        multipart.readAllParts().forEach {
            if (it is PartData.FileItem) {
                val fileBytes = it.streamProvider().readBytes()

                if (fileBytes.isEmpty() || fileBytes.size >= 2000000) {
                    throw IllegalArgumentException("File size should not be 0 or greater than 2 mb")
                }

                imageFile = File(imageFile, "${UUID.randomUUID()}-${it.originalFileName}")
                imageFile.writeBytes(fileBytes)

                if (!imageFile.exists()) {
                    throw Exception("Image doesn't exist")
                }
            }
            return@forEach
        }

        return imageFile
    }

    fun deleteUser(userId: Int): Boolean {
        return userRepository.deleteById(userId)
    }
}