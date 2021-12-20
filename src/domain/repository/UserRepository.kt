package io.ducket.api.domain.repository

import domain.model.account.AccountsTable
import io.ducket.api.domain.controller.user.UserSignUpDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import domain.model.currency.CurrencyEntity
import domain.model.imports.ImportRulesTable
import domain.model.imports.ImportsTable
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import domain.model.user.User
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.budget.BudgetsTable
import io.ducket.api.domain.model.follow.FollowEntity
import io.ducket.api.domain.model.follow.FollowsTable
import io.ducket.api.domain.model.transfer.TransferEntity
import io.ducket.api.domain.model.transfer.TransfersTable

import io.ducket.api.domain.model.user.UserAttachmentsTable
import io.ducket.api.getLogger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.time.Instant

class UserRepository {
    private val logger = getLogger()

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

    fun findUsersFollowingByUser(userId: Long): List<User> = transaction {
        FollowEntity.wrapRows(
            FollowsTable.select {
                FollowsTable.followerUserId.eq(userId)
                    .and(FollowsTable.isPending.eq(false))
            }
        ).toList().map { it.followedUser.toModel() }
    }

    fun findOneByEmail(email: String): User? = transaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    fun findOne(userId: Long): User? = transaction {
        UserEntity.findById(userId)?.toModel()
    }

    fun findAll(): List<User> = transaction {
        UserEntity.all().map { it.toModel() }
    }

    fun updateOne(userId: Long, dto: UserUpdateDto): User? = transaction {
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

    fun deleteOne(userId: Long): Boolean = transaction {
        UsersTable.deleteWhere { UsersTable.id.eq(userId) } > 0
    }

    fun deleteImage(imageId: Long): Boolean = transaction {
        UserAttachmentsTable.deleteWhere { UserAttachmentsTable.attachmentId.eq(imageId) } > 0
    }

    fun findImage(userId: Long, imageId: Long): Attachment? = transaction {
        val query = AttachmentsTable.innerJoin(UserAttachmentsTable).innerJoin(UsersTable)
            .slice(AttachmentsTable.columns)
            .select {
                AttachmentsTable.id.eq(imageId)
                    .and(UsersTable.id.eq(userId))
                    .and(UserAttachmentsTable.userId.eq(UsersTable.id))
            }

        AttachmentEntity.wrapRows(query).firstOrNull()?.toModel()
    }

    fun createImage(userId: Long, newFile: File): Unit = transaction {
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

    /**
     * Wipe out user data:
     * transactions, transfers, budgets, import rules, imports and accounts
     */
    fun deleteUserData(userId: Long): Boolean = transaction {
        try {
            TransactionEntity.find { TransactionsTable.userId.eq(userId) }.forEach { transaction ->
                transaction.attachments.forEach { it.delete() }
                transaction.delete()
            }

            TransferEntity.find { TransfersTable.userId.eq(userId) }.forEach { transfer ->
                transfer.attachments.forEach { it.delete() }
                transfer.delete()
            }

            BudgetsTable.deleteWhere { BudgetsTable.userId.eq(userId) }
            ImportRulesTable.deleteWhere { ImportRulesTable.userId.eq(userId) }
            ImportsTable.deleteWhere { ImportsTable.userId.eq(userId) }
            AccountsTable.deleteWhere { AccountsTable.userId.eq(userId) }

            return@transaction true
        } catch (e: Exception) {
            TransactionManager.current().rollback()
            logger.error(e.localizedMessage)
            return@transaction false
        }
    }
}
