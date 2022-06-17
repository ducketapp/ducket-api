package io.ducket.api.domain.model.group

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import io.ducket.api.app.AccountPermission
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object GroupMemberAccountPermissionsTable : LongIdTable("group_member_account_permission") {
    val membershipId = reference("membership_id", GroupMembershipsTable)
    val accountId = reference("account_id", AccountsTable)
    val accountPermission = enumerationByName("account_permission", 32, AccountPermission::class)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class GroupMemberAccountPermissionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupMemberAccountPermissionEntity>(GroupMemberAccountPermissionsTable)

    var membership by GroupMembershipEntity referencedOn GroupMemberAccountPermissionsTable.membershipId
    var account by AccountEntity referencedOn GroupMemberAccountPermissionsTable.accountId
    var accountPermission by GroupMemberAccountPermissionsTable.accountPermission
    var createdAt by GroupMemberAccountPermissionsTable.createdAt
    var modifiedAt by GroupMemberAccountPermissionsTable.modifiedAt

    fun toModel() = GroupMemberAccountPermission(
        id.value,
        account.toModel(),
        accountPermission,
        createdAt,
        modifiedAt,
    )
}

data class GroupMemberAccountPermission(
    val id: Long,
    val account: Account,
    val accountPermission: AccountPermission,
    val createdAt: Instant,
    val modifiedAt: Instant,
)