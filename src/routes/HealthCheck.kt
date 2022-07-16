package dev.ducket.api.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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