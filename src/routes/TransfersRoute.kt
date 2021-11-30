package io.ducket.api.routes

import io.ducket.api.domain.controller.transfer.TransferController
import io.ktor.auth.*
import io.ktor.routing.*

// TODO add delete multiple transfers
// TODO add delete multiple transfer attachments
fun Route.transfers(transferController: TransferController) {
    authenticate {
        route("/transfers") {
            post { transferController.addTransfer(this.context) }

            route("/{transferId}") {
                delete { transferController.getTransfer(this.context) }
                delete { transferController.deleteTransfer(this.context) }

                route("/attachments") {
                    post { transferController.uploadTransferAttachments(this.context) }

                    route("/{attachmentId}") {
                        get { transferController.downloadTransferAttachment(this.context) }
                    }
                }
            }
        }
    }
}