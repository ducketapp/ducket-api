package io.ducket.api.routes

import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.healthCheck() {
    route("/health-check") {
        get {
            this.context.respondText(
                text = "Status: up and running!",
                status = HttpStatusCode.OK
            )
        }
    }
}