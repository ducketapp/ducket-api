package io.ducket.api.routes

import io.ducket.api.auth.UserRole
import io.ducket.api.auth.authorization.authorize
import io.ducket.api.domain.controller.ledger.LedgerController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.ledgerRecords(
    ledgerController: LedgerController,
) {
    authenticate {
        route("/records") {
            get { ledgerController.getLedgerRecords(this.context) }

            authorize(UserRole.SUPER_USER) {
                post { ledgerController.createLedgerRecord(this.context) }
                // delete { ledgerController.deleteLedgerRecords(this.context) }
            }

            route("/{recordId}") {
                get { ledgerController.getLedgerRecord(this.context) }

                authorize(UserRole.SUPER_USER) {
                    delete { ledgerController.deleteLedgerRecord(this.context) }
                }

                route("/operations") {
                    route("/{operationId}") {
                        route("/images") {
                            authorize(UserRole.SUPER_USER) {
                                post { ledgerController.uploadLedgerRecordAttachments(this.context) }
                            }

                            route("/{imageId}") {
                                get { ledgerController.downloadLedgerRecordAttachment(this.context) }

                                authorize(UserRole.SUPER_USER) {
                                    delete { ledgerController.deleteLedgerRecordAttachment(this.context) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}