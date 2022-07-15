package io.ducket.api.domain.controller.group

import io.ducket.api.auth.JwtManager
import io.ducket.api.auth.UserPrincipal
import io.ducket.api.auth.UserRole
import io.ducket.api.domain.service.GroupService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import org.koin.java.KoinJavaComponent.inject

class GroupController(private val groupService: GroupService) {
    private val jwtManager: JwtManager by inject(JwtManager::class.java)

    suspend fun addGroup(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<GroupCreateDto>().apply {
            groupService.createGroup(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getGroups(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        groupService.getGroups(userId).apply {
            ctx.respond(HttpStatusCode.OK, this)
        }
    }

    suspend fun getGroup(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        groupService.getGroup(userId, groupId).run {
            if (owner.id != userId) {
                ctx.response.header(
                    name = HttpHeaders.Authorization,
                    value = jwtManager.getAuthorizationHeaderValue(
                        UserPrincipal(
                            id = owner.id,
                            email = owner.email,
                            role = UserRole.SHARED_USER
                        )
                    ),
                )
            }

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

    suspend fun deleteGroup(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        groupService.deleteGroup(userId, groupId)

        ctx.respond(HttpStatusCode.NoContent)
    }

//    suspend fun getGroupMembership(ctx: ApplicationCall) {
//        val user = ctx.authentication.principalOrThrow()
//        val groupId = ctx.parameters.getOrFail("groupId").toLong()
//
//        groupService.getGroupMembership(user, groupId).apply {
//            ctx.respond(HttpStatusCode.OK, this)
//        }
//    }

    suspend fun addGroupMember(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()

        ctx.receive<GroupMemberCreateDto>().apply {
            groupService.addGroupMember(userId, groupId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteGroupMember(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()
        val membershipId = ctx.parameters.getOrFail("membershipId").toLong()

        groupService.deleteGroupMember(userId, groupId, membershipId)

        ctx.respond(HttpStatusCode.NoContent)
    }

    suspend fun updateGroupMember(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val groupId = ctx.parameters.getOrFail("groupId").toLong()
        val membershipId = ctx.parameters.getOrFail("membershipId").toLong()

        ctx.receive<GroupMemberUpdateDto>().let { payload ->
            groupService.updateGroupMember(userId, groupId, membershipId, payload.validate()).run {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }
}