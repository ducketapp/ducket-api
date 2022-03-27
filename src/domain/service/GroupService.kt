package io.ducket.api.domain.service

import io.ducket.api.app.MembershipAction
import io.ducket.api.app.MembershipStatus
import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.group.*
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.group.GroupMembership
import io.ducket.api.domain.repository.GroupRepository
import io.ducket.api.domain.repository.GroupMembershipRepository
import io.ducket.api.domain.repository.UserRepository
import io.ducket.api.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction

class GroupService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val groupMembershipRepository: GroupMembershipRepository,
) {

    /**
     * Create a group & memberships
     *
     * @param userId id of the user who creates the group
     * @param payload request object to create the group
     * @throws DuplicateEntityException if the group with desired name already exists
     * @return group & memberships data
     */
    fun createGroup(userId: Long, payload: GroupCreateDto): GroupDetailsDto {
        groupRepository.findOneByCreatorAndName(userId, payload.name)?.let {
            throw DuplicateEntityException()
        }

        return transaction {
            groupRepository.create(userId, payload).let { newGroup ->
                // create active membership for group creator by default
                val groupMembership = groupMembershipRepository.create(
                    groupId = newGroup.id,
                    dto = GroupMembershipCreateDto(
                        memberEmail = newGroup.creator.email,
                    ),
                    status = MembershipStatus.ACTIVE
                )

                // create pending memberships for the rest group members
                val otherGroupMemberships = payload.members
                    .filter { it.memberEmail != groupMembership.member.email }
                    .map { reqObj ->
                        groupMembershipRepository.create(
                            groupId = newGroup.id,
                            dto = reqObj,
                            status = MembershipStatus.PENDING,
                        )
                }

                return@transaction GroupDetailsDto(
                    group = newGroup,
                    membership = groupMembership,
                    otherMemberships = otherGroupMemberships
                )
            }
        }
    }

    /**
     * Update a group
     *
     * @param userId id of the creator of the group
     * @param groupId id of the existing group to update
     * @param payload request object to update the group
     * @throws BusinessLogicException if the user is not the creator of the group
     * @throws NoEntityFoundException if the user does not have group membership
     * @throws DuplicateEntityException if the group with desired name already exists
     * @return group & memberships data
     */
    fun updateGroup(userId: Long, groupId: Long, payload: GroupUpdateDto): GroupDetailsDto {
        val groupMembership = groupMembershipRepository.findOneByGroupAndMember(groupId, userId)?.also {
            if (it.group.creator.id != userId) {
                throw BusinessLogicException("Cannot perform an action not being a group creator")
            }
        } ?: throw NoEntityFoundException()

        payload.name?.also {
            groupRepository.findOneByCreatorAndName(userId, it)?.let { found ->
                if (found.id == groupId) throw DuplicateEntityException()
            }
        }

        return groupRepository.updateOne(userId, groupId, payload)?.let {
            GroupDetailsDto(
                group = it,
                membership = groupMembership,
                otherMemberships = getGroupMembershipsExcludingMember(userId, groupId),
            )
        } ?: throw NoEntityFoundException()
    }

    fun applyGroupMembershipAction(userId: Long, groupId: Long, membershipId: Long, payload: GroupMembershipActionDto) {
        groupMembershipRepository.findOne(userId, membershipId)?.let {
            if (it.status == MembershipStatus.ACTIVE && payload.action == MembershipAction.ACCEPT) {
                throw BusinessLogicException("Cannot perform an action being an active member of the group")
            }
        } ?: throw NoEntityFoundException("No membership found for such group")

        when (payload.action) {
            MembershipAction.ACCEPT -> groupMembershipRepository.update(userId, groupId, membershipId, MembershipStatus.ACTIVE)
            MembershipAction.CANCEL -> groupMembershipRepository.delete(groupId, membershipId)
        }
    }

    /**
     * Create a group membership in pending status
     *
     * @param userId id of the creator of the group
     * @param groupId id of the existing group
     * @param payload request object to create group membership
     * @throws BusinessLogicException if the user is not the creator of the group
     * @throws NoEntityFoundException if the group or the requested member doesn't exist
     * @throws DuplicateEntityException if the member of the group already has a membership
     * @return group & memberships data
     */
    fun createGroupMembership(userId: Long, groupId: Long, payload: GroupMembershipCreateDto): GroupDetailsDto {
        val groupMembership = groupMembershipRepository.findOneByGroupAndMember(groupId, userId)?.also {
            if (it.group.creator.id != userId) {
                throw BusinessLogicException("Cannot perform an action not being a group creator")
            }
        } ?: throw NoEntityFoundException()

        userRepository.findOneByEmail(payload.memberEmail)?.also { foundUser ->
            groupMembershipRepository.findOneByGroupAndMember(groupId, foundUser.id)?.let {
                throw DuplicateEntityException("${foundUser.name} already has ${it.status.name.lowercase()} membership")
            }
        } ?: throw NoEntityFoundException("No user found with ${payload.memberEmail} email")

        groupMembershipRepository.create(groupId, payload, MembershipStatus.PENDING)

        return GroupDetailsDto(
            group = groupMembership.group,
            membership = groupMembership,
            otherMemberships = getGroupMembershipsExcludingMember(userId, groupId),
        )
    }

    fun deleteGroupMemberships(userId: Long, groupId: Long, payload: GroupMembershipDeleteDto): GroupDetailsDto {
        val groupMembership = groupMembershipRepository.findOneByGroupAndMember(groupId, userId)?.also { membership ->
            if (membership.group.creator.id != userId) {
                throw BusinessLogicException("The action requires the rights of the creator")
            } else {
                if (payload.membershipIds.contains(membership.id)) {
                    throw BusinessLogicException("The action cannot be applied to the group creator")
                }
            }
        } ?: throw NoEntityFoundException()

        groupMembershipRepository.delete(groupId, *payload.membershipIds.toLongArray())

        return GroupDetailsDto(
            group = groupMembership.group,
            membership = groupMembership,
            otherMemberships = getGroupMembershipsExcludingMember(userId, groupId),
        )
    }

    /**
     * Get groups by member
     * Pending group memberships are returned if the user is the creator of the group
     *
     * @param userId id of the member of the group
     */
    fun getGroupsByMember(userId: Long): List<GroupDetailsDto> {
        val groupMemberships = groupMembershipRepository.findAllByMember(userId)

        return groupMemberships.map { groupMembership ->
            val otherGroupMemberships = getGroupMembershipsExcludingMember(groupMembership.member.id, groupMembership.group.id)

            GroupDetailsDto(
                group = groupMembership.group,
                membership = groupMembership,
                otherMemberships = otherGroupMemberships,
            )
        }
    }

    /**
     * Get group by member
     * Pending group memberships are returned if the user is the creator of the group
     *
     * @param userId id of the member of the group
     * @param groupId id of the group
     * @throws NoEntityFoundException if no user membership found for the group
     */
    fun getGroupByMember(userId: Long, groupId: Long): GroupDetailsDto {
        val groupMembership = groupMembershipRepository.findOneByGroupAndMember(groupId, userId)
            ?: throw NoEntityFoundException("No membership found for the group")

        val otherGroupMemberships = getGroupMembershipsExcludingMember(groupMembership.member.id, groupId)

        return GroupDetailsDto(
            group = groupMembership.group,
            membership = groupMembership,
            otherMemberships = otherGroupMemberships,
        )
    }

    fun deleteGroups(userId: Long, payload: BulkDeleteDto) {
        groupRepository.findAllByMember(userId)
            .filter { payload.ids.contains(it.id) }
            .find { it.creator.id != userId }
            ?.let {
                throw BusinessLogicException("Cannot perform an action for '${it.name}' without being a group creator")
            }

        return transaction {
            // delete all groups memberships
            payload.ids.forEach { groupMembershipRepository.deleteAll(it) }
            // delete groups
            groupRepository.delete(userId, *payload.ids.toLongArray())
        }
    }

    fun deleteGroup(userId: Long, groupId: Long) {
        groupRepository.findOne(groupId)?.also {
            if (it.creator.id != userId) throw BusinessLogicException("Cannot perform an action without being a group creator")
        } ?: throw NoEntityFoundException()

        return transaction {
            // delete all group memberships
            groupMembershipRepository.deleteAll(groupId)
            // delete group
            groupRepository.delete(userId, groupId)
        }
    }

    fun getDistinctUsersWithMutualGroupMemberships(userId: Long): List<UserDto> {
        return getGroupsByMember(userId)
            .flatMap { it.otherMemberships }
            .filter { it.status == MembershipStatus.ACTIVE }
            .distinctBy { it.id }
            .map { it.member }
    }

    private fun getGroupMembershipsExcludingMember(memberId: Long, groupId: Long): List<GroupMembership> {
        return groupMembershipRepository.findAllByGroup(groupId)
            .filter { it.member.id != memberId }
            .filter { if (it.group.creator.id != memberId) it.status == MembershipStatus.ACTIVE else true }
            .sortedByDescending { it.status }
    }
}