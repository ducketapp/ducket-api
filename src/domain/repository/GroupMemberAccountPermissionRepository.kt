package io.ducket.api.domain.repository

import domain.model.account.AccountEntity
import io.ducket.api.app.AccountPermission
import io.ducket.api.domain.model.group.*
import io.ducket.api.domain.model.group.GroupMemberAccountPermissionsTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class GroupMemberAccountPermissionRepository {

    fun create(membershipId: Long, accountId: Long, accountPermission: AccountPermission): GroupMemberAccountPermission = transaction {
        GroupMemberAccountPermissionEntity.new {
            this.membership = GroupMembershipEntity[membershipId]
            this.account = AccountEntity[accountId]
            this.accountPermission = accountPermission
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }

    fun updateOne(permissionId: Long, permissionType: AccountPermission): GroupMemberAccountPermission? = transaction {
        GroupMemberAccountPermissionEntity.findById(permissionId)?.also { found ->
            found.accountPermission = permissionType
            found.modifiedAt = Instant.now()
        }?.toModel()
    }

    fun findAllByMembership(membershipId: Long): List<GroupMemberAccountPermission> = transaction {
        GroupMemberAccountPermissionEntity.find { GroupMemberAccountPermissionsTable.membershipId.eq(membershipId) }.map { it.toModel() }
    }
}