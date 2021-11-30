package io.ducket.api.domain.repository

import io.ducket.api.domain.controller.user.UserSignUpDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import domain.model.currency.CurrencyEntity
import domain.model.user.User
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.follow.Follow
import io.ducket.api.domain.model.follow.FollowEntity
import io.ducket.api.domain.model.follow.FollowsTable

import io.ducket.api.domain.model.user.UserAttachmentsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.time.Instant

class UserRepository {

    fun create(dto: UserSignUpDto): User = transaction {
        UserEntity.new {
            name = dto.name
            phone = dto.phone
            email = dto.email
            mainCurrency = CurrencyEntity[dto.currencyId]
            passwordHash = BCrypt.hashpw(dto.password, BCrypt.gensalt(10))
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun findOneByEmail(email: String): User? = transaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    fun findOne(userId: String): User? = transaction {
        UserEntity.findById(userId)?.toModel()
    }

    fun updateOne(userId: String, dto: UserUpdateDto): User? = transaction {
        UserEntity.findById(userId)?.also { found ->
            dto.name?.let {
                found.name = it
                found.modifiedAt = Instant.now()
            }

            dto.password?.let {
                found.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt(10))
                found.modifiedAt = Instant.now()
            }
        }?.toModel()
    }

    fun deleteOne(userId: String): Boolean = transaction {
        UsersTable.deleteWhere { UsersTable.id.eq(userId) } > 0
    }

    fun findImage(userId: String, imageId: String): Attachment? = transaction {
        val query = AttachmentsTable.innerJoin(UserAttachmentsTable).innerJoin(UsersTable)
            .slice(AttachmentsTable.columns)
            .select {
                AttachmentsTable.id.eq(imageId)
                    .and(UsersTable.id.eq(userId))
                    .and(UserAttachmentsTable.userId.eq(UsersTable.id))
            }

        AttachmentEntity.wrapRows(query).firstOrNull()?.toModel()
    }

    fun createImage(userId: String, newFile: File): Unit = transaction {
        val newAttachment = AttachmentEntity.new {
            filePath = newFile.path
            createdAt = Instant.now()
        }.toModel()

        UserAttachmentsTable.insert {
            it[this.attachmentId] = AttachmentEntity[newAttachment.id].id.value
            it[this.userId] = UserEntity[userId].id.value
        }

        UserEntity.findById(userId)?.also {
            it.modifiedAt = Instant.now()
        }
    }

    fun deleteImage(imageId: String): Boolean = transaction {
        UserAttachmentsTable.deleteWhere { UserAttachmentsTable.attachmentId.eq(imageId) } > 0
    }
}
