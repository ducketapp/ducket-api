package io.ducket.api.routes

import io.ducket.api.domain.controller.label.LabelController
import io.ktor.auth.*
import io.ktor.routing.*

// TODO add delete label(s)
fun Route.labels(labelController: LabelController) {
    authenticate {
        route("/labels") {
            post { labelController.createLabel(this.context) }
            get { labelController.getLabels(this.context) }
        }
    }
}