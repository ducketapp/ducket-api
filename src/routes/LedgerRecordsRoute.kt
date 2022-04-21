package io.ducket.api.routes

import io.ducket.api.domain.controller.ledger.LedgerController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.ledgerRecords(
    ledgerController: LedgerController,
) {
    authenticate {
        route("records") {
            post { ledgerController.createLedgerRecord(this.context) }
            get { ledgerController.getLedgerRecords(this.context) }
            // delete { ledgerController.deleteLedgerRecords(this.context) }

            route("{recordId}") {
                get { ledgerController.getLedgerRecord(this.context) }
                delete { ledgerController.deleteLedgerRecord(this.context) }

                route("operations") {
                    route("{operationId}") {
                        route("images") {
                            post { ledgerController.uploadLedgerRecordAttachments(this.context) }

                            route("{imageId}") {
                                get { ledgerController.downloadLedgerRecordAttachment(this.context) }
                                delete { ledgerController.deleteLedgerRecordAttachment(this.context) }
                            }
                        }
                    }
                }
            }
        }
    }
}