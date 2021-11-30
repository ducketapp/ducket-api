package io.ducket.api.domain.controller.label

import io.ducket.api.config.JwtConfig
import io.ducket.api.domain.service.LabelService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*

class LabelController(val labelService: LabelService) {

    suspend fun createLabel(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        ctx.receive<LabelCreateDto>().apply {
            labelService.createLabel(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getLabels(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id.toString()

        val labels = labelService.getLabels(userId)
        ctx.respond(HttpStatusCode.OK, labels)
    }
}