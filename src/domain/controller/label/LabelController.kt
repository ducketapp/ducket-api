package io.budgery.api.domain.controller.label

import io.budgery.api.config.JwtConfig
import io.budgery.api.domain.service.LabelService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*

class LabelController(
    val labelService: LabelService,
) {

    suspend fun createUserLabel(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        ctx.receive<LabelCreateDto>().apply {
            labelService.createLabel(userId, this.validate()).apply {
                ctx.respond(HttpStatusCode.Created, this)
            }
        }
    }

    suspend fun getUserLabels(ctx: ApplicationCall) {
        val userId = JwtConfig.getPrincipal(ctx.authentication).id

        val labels = labelService.getLabels(userId)
        ctx.respond(HttpStatusCode.OK, labels)
    }
}