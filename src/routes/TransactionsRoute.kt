package io.ducket.api.routes

import io.ducket.api.domain.controller.transaction.TransactionController
import io.ktor.auth.*
import io.ktor.routing.*

// TODO add delete multiple
fun Route.transactions(transactionController: TransactionController) {
    authenticate {
        route("/transactions") {
            post { transactionController.addTransaction(this.context) }
            patch { transactionController.deleteTransactions(this.context) }

            route("/{transactionId}") {
                get { transactionController.getTransaction(this.context) }
                delete { transactionController.deleteTransaction(this.context) }

                route("/attachments") {
                    post { transactionController.uploadTransactionAttachments(this.context) }

                    route("/{attachmentId}") {
                        get { transactionController.downloadTransactionAttachment(this.context) }
                        delete { transactionController.deleteTransactionAttachment(this.context) }
                    }
                }
            }
        }
    }
}