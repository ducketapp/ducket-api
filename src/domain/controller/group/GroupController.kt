package io.ducket.api.domain.controller.group

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.service.GroupService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class GroupController(
    private val groupService: GroupService,
) {

    suspend fun createGroup(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<GroupCreateDto>().apply {
            groupService.createGroup(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getGroups(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        groupService.getGroupsByMember(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getGroup(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        groupService.getGroupByMember(userId, groupId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun updateGroup(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        ctx.receive<GroupUpdateDto>().apply {
            groupService.updateGroup(userId, groupId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteGroups(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().apply {
            groupService.deleteGroups(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }

    suspend fun deleteGroup(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        groupService.deleteGroup(userId, groupId)

        ctx.respond(HttpStatusCode.NoContent)
    }

    suspend fun addGroupMembership(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        ctx.receive<GroupMembershipCreateDto>().apply {
            groupService.createGroupMembership(userId, groupId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun deleteGroupMemberships(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        ctx.receive<GroupMembershipDeleteDto>().apply {
            groupService.deleteGroupMemberships(userId, groupId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun updateGroupMembership(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()
        val membershipId = ctx.parameters.getOrFail("membershipId").toLong()

        ctx.receive<GroupMembershipActionDto>().apply {
            groupService.applyGroupMembershipAction(userId, groupId, membershipId, this.validate()).apply {
                ctx.respond(HttpStatusCode.NoContent)
            }
        }
    }
}