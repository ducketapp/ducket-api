package io.ducket.api.routes

import io.ducket.api.domain.controller.record.RecordController
import io.ducket.api.domain.controller.transaction.TransactionController
import io.ducket.api.domain.controller.transfer.TransferController
import io.ducket.api.domain.controller.user.UserController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.records(
    recordController: RecordController,
    transactionController: TransactionController,
    transferController: TransferController,
    userController: UserController,
) {
    authenticate {
        route("records") {
            get { recordController.getRecords(this.context) }

            route("transactions") {
                post { transactionController.addTransaction(this.context) }
                patch { transactionController.deleteTransactions(this.context) }

                route("{transactionId}") {
                    get { transactionController.getTransaction(this.context) }
                    delete { transactionController.deleteTransaction(this.context) }

                    route("images") {
                        post { transactionController.uploadTransactionAttachments(this.context) }

                        route("{imageId}") {
                            get { transactionController.downloadTransactionAttachment(this.context) }
                            delete { transactionController.deleteTransactionAttachment(this.context) }
                        }
                    }
                }
            }

            route("transfers") {
                post { transferController.addTransfer(this.context) }

                route("{transferId}") {
                    delete { transferController.getTransfer(this.context) }
                    delete { transferController.deleteTransfer(this.context) }

                    route("images") {
                        post { transferController.uploadTransferAttachments(this.context) }

                        route("{imageId}") {
                            get { transferController.downloadTransferAttachment(this.context) }
                            delete { transferController.deleteTransferAttachment(this.context) }
                        }
                    }
                }
            }
        }
    }
}