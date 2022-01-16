package io.ducket.api.routes

import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.root() {
    route("/") {
        get("/info") {
            this.context.respond(HttpStatusCode.OK,
                """
                Hello from Ktor Netty engine
                - Available processors: ${Runtime.getRuntime().availableProcessors()}
                - Free memory: ${Runtime.getRuntime().freeMemory()}
                - Total memory: ${Runtime.getRuntime().totalMemory()}    
                - Max memory: ${Runtime.getRuntime().maxMemory()}
                """.trimIndent())
        }
    }
}