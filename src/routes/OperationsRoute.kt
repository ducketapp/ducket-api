package io.ducket.api.routes

import io.ducket.api.domain.controller.operation.OperationController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.operations(operationController: OperationController) {
    authenticate {
//        route("/operations") {
//            get { operationController.getOperations(this.context) }
//
//            authorize(UserRole.SUPER_USER) {
//                post { operationController.createOperation(this.context) }
//                // delete { operationController.deleteOperations(this.context) }
//            }
//
//            route("/{operationId}") {
//                get { operationController.getOperation(this.context) }
//
//                authorize(UserRole.SUPER_USER) {
//                    delete { operationController.deleteOperation(this.context) }
//                }
//
//                route("/images") {
//                    authorize(UserRole.SUPER_USER) {
//                        post { operationController.uploadOperationAttachments(this.context) }
//                    }
//
//                    route("/{imageId}") {
//                        get { operationController.downloadOperationAttachment(this.context) }
//
//                        authorize(UserRole.SUPER_USER) {
//                            delete { operationController.deleteOperationAttachment(this.context) }
//                        }
//                    }
//                }
//            }
//        }
    }
}