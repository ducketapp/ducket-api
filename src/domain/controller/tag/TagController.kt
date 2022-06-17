package io.ducket.api.domain.controller.tag

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.service.TagService
import io.ducket.api.principalOrThrow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

class TagController(
    private val tagService: TagService
) {

    suspend fun getTags(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val tags = tagService.getTags(userId)

        ctx.respond(HttpStatusCode.OK, tags)
    }

    suspend fun createTag(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<TagCreateDto>().apply {
            tagService.createTag(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun deleteTags(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().apply {
            tagService.deleteTags(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.NoContent, this)
            }
        }
    }

    suspend fun getTag(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val tagId = ctx.parameters.getOrFail("tagId").toLong()

        val tag = tagService.getTag(userId, tagId)
        ctx.respond(HttpStatusCode.OK, tag)
    }

    suspend fun updateTag(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val tagId = ctx.parameters.getOrFail("tagId").toLong()

        ctx.receive<TagUpdateDto>().apply {
            tagService.updateTag(userId, tagId, this.validate()).apply {
                ctx.respond(HttpStatusCode.OK, this)
            }
        }
    }

    suspend fun deleteTag(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val tagId = ctx.parameters.getOrFail("tagId").toLong()

        tagService.deleteTag(userId, tagId)
        ctx.respond(HttpStatusCode.NoContent)
    }
}