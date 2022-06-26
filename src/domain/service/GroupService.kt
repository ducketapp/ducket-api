package io.ducket.api.domain.service

import io.ducket.api.app.AccountPermission
import io.ducket.api.domain.controller.group.*
import io.ducket.api.domain.repository.*
import io.ducket.api.plugins.BusinessLogicException
import io.ducket.api.plugins.DuplicateDataException
import io.ducket.api.plugins.NoDataFoundException
import org.jetbrains.exposed.sql.transactions.transaction

class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMembershipRepository: GroupMembershipRepository,
    private val groupMemberAccountPermissionRepository: GroupMemberAccountPermissionRepository,
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository,
) {
    private val maxGroups: Int = 1
    private val maxGroupMembers: Int = 5

    fun createGroup(userId: Long, payload: GroupCreateDto): GroupDto {
        val user = userRepository.findOne(userId)!!

        if (groupRepository.findAllByOwner(user.id).any { it.owner.id == user.id }) {
            throw BusinessLogicException("It is allowed to own only up to $maxGroups group(s)")
        }

        return GroupDto(groupRepository.create(user.id, payload.name))
    }

    fun updateGroup(userId: Long, groupId: Long, payload: GroupUpdateDto): GroupDto {
        groupRepository.findOneByOwner(userId, groupId) ?: throw NoDataFoundException()
        return groupRepository.updateOne(userId, groupId, payload.name)?.let { GroupDto(it) }!!
    }

    fun deleteGroup(userId: Long, groupId: Long) {
        groupRepository.delete(userId, groupId)
    }

    fun getGroups(userId: Long): List<GroupDto> {
        val user = userRepository.findOne(userId)!!
        return getOwnedGroups(user.id) + getSharedGroups(user.email)
    }

    fun getGroup(userId: Long, groupId: Long): GroupDto {
        return getGroups(userId).firstOrNull { it.id == groupId } ?: throw NoDataFoundException()
    }

    fun addGroupMember(userId: Long, groupId: Long, payload: GroupMemberCreateDto): GroupDto {
        val group = groupRepository.findOneByOwner(userId, groupId) ?: throw NoDataFoundException()

        if (group.owner.email == payload.memberEmail) {
            throw BusinessLogicException("Group owner is not entitled to be a member of sharing group")
        }

        if (groupMembershipRepository.findAllByOwnerAndGroup(userId, groupId).count() == maxGroupMembers) {
            throw BusinessLogicException("It is allowed to have only up to $maxGroupMembers in a group")
        }

        groupMembershipRepository.findOneByEmailAndGroup(payload.memberEmail, groupId)?.also {
            throw DuplicateDataException("Such a member already added to the group")
        }

        // No email checks for potential member in order to not reveal the registered emails
        transaction {
            val newGroupMembership = groupMembershipRepository.create(groupId, payload)

            accountRepository.findAll(userId).map { it.id }.forEach { accountId ->
                groupMemberAccountPermissionRepository.create(newGroupMembership.id, accountId, AccountPermission.READ_ONLY)
            }
        }

        return getGroup(userId, groupId)
    }

    fun updateGroupMember(userId: Long, groupId: Long, membershipId: Long, payload: GroupMemberUpdateDto): GroupDto {
        groupMembershipRepository.findOneByGroupOwnerAndGroup(userId, groupId, membershipId) ?: throw NoDataFoundException()

        transaction {
            payload.accountPermissions.forEach {
                groupMemberAccountPermissionRepository.updateOne(it.permissionId, it.accountPermission)
            }
        }

        return getGroup(userId, groupId)
    }

    fun deleteGroupMember(userId: Long, groupId: Long, membershipId: Long) {
        groupMembershipRepository.delete(userId, groupId, membershipId)
    }

    private fun getSharedGroups(userEmail: String): List<GroupDto> {
        return groupRepository.findAllByMember(userEmail).map { group ->
            val userGroupMembership = group.memberships.firstOrNull { it.memberEmail == userEmail }!!
            val groupWithUserMembershipOnly = group.copy(memberships = listOf(userGroupMembership))

            return@map GroupDto(groupWithUserMembershipOnly)
        }
    }

    private fun getOwnedGroups(userId: Long): List<GroupDto> {
        return groupRepository.findAllByOwner(userId).map { GroupDto(it) }
    }
}