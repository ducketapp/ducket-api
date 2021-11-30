package io.ducket.api.routes

import io.ducket.api.domain.controller.record.RecordController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.records(recordController: RecordController) {
    authenticate {
        route("/records") {
            get { recordController.getRecords(this.context) }
        }
    }
}