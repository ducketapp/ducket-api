package io.ducket.api.domain.controller.tag

import io.ducket.api.domain.controller.BulkDeleteDto
import io.ducket.api.domain.controller.tag.dto.TagCreateUpdateDto
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

        tagService.getTags(userId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)

        }
    }

    suspend fun createTag(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<TagCreateUpdateDto>().let { reqObj ->
            tagService.createTag(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.Created, resObj)
            }
        }
    }

    suspend fun deleteTags(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id

        ctx.receive<BulkDeleteDto>().let { reqObj ->
            tagService.deleteTags(userId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.NoContent, resObj)
            }
        }
    }

    suspend fun getTag(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val tagId = ctx.parameters.getOrFail("tagId").toLong()

        tagService.getTag(userId, tagId).let { resObj ->
            ctx.respond(HttpStatusCode.OK, resObj)
        }
    }

    suspend fun updateTag(ctx: ApplicationCall) {
        val userId = ctx.authentication.principalOrThrow().id
        val tagId = ctx.parameters.getOrFail("tagId").toLong()

        ctx.receive<TagCreateUpdateDto>().let { reqObj ->
            tagService.updateTag(userId, tagId, reqObj.validate()).let { resObj ->
                ctx.respond(HttpStatusCode.OK, resObj)
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