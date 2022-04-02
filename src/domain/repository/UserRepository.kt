package io.ducket.api.domain.repository

import domain.model.account.AccountsTable
import domain.model.currency.CurrenciesTable
import io.ducket.api.domain.controller.user.UserCreateDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import domain.model.currency.CurrencyEntity
import domain.model.imports.ImportRulesTable
import domain.model.imports.ImportsTable
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import domain.model.user.User
import io.ducket.api.BCRYPT_HASH_ROUNDS
import io.ducket.api.domain.model.budget.BudgetsTable
import io.ducket.api.domain.model.group.GroupEntity
import io.ducket.api.domain.model.group.GroupMembershipsTable
import io.ducket.api.domain.model.group.GroupsTable
import io.ducket.api.domain.model.transaction.TransactionAttachmentsTable
import io.ducket.api.domain.model.transfer.TransferAttachmentsTable
import io.ducket.api.domain.model.transfer.TransferEntity
import io.ducket.api.domain.model.transfer.TransfersTable

import io.ducket.api.getLogger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

class UserRepository {
    private val logger = getLogger()

    fun create(dto: UserCreateDto): User = transaction {
        UserEntity.new {
            name = dto.name
            phone = dto.phone
            email = dto.email
            mainCurrency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(dto.currencyIsoCode) }.first()
            passwordHash = BCrypt.hashpw(dto.password, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
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
                found.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))
                found.modifiedAt = Instant.now()
            }
        }?.toModel()
    }

    fun deleteData(userId: Long): Unit = transaction {
        // Delete transaction's attachments
        // Delete transactions
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
        }.map { it.id.value }.also {
            TransactionAttachmentsTable.deleteWhere { TransactionAttachmentsTable.transactionId.inList(it) }
            TransactionsTable.deleteWhere { TransactionsTable.id.inList(it) }
        }

        // Delete transfer's attachments
        // Delete transfers
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
        }.map { it.id.value }.also {
            TransferAttachmentsTable.deleteWhere { TransferAttachmentsTable.transferId.inList(it) }
            TransfersTable.deleteWhere { TransfersTable.id.inList(it) }
        }

        // Delete owned groups & included group memberships
        // Delete group memberships
        GroupEntity.find {
            GroupsTable.creatorId.eq(userId)
        }.map { it.id.value }.also {
            GroupMembershipsTable.deleteWhere { GroupMembershipsTable.groupId.inList(it).or(GroupMembershipsTable.memberId.eq(userId)) }
            GroupsTable.deleteWhere { GroupsTable.id.inList(it) }
        }

        BudgetsTable.deleteWhere { BudgetsTable.userId.eq(userId) }
        ImportsTable.deleteWhere { ImportsTable.userId.eq(userId) }
        ImportRulesTable.deleteWhere { ImportRulesTable.userId.eq(userId) }
        AccountsTable.deleteWhere { AccountsTable.userId.eq(userId) }
    }

    fun deleteOne(userId: Long): Unit = transaction {
        UserEntity.findById(userId)!!.delete()
    }
}
