package org.expenny.service.routes

import org.expenny.service.auth.authentication.UserRole
import org.expenny.service.auth.authorization.authorize
import org.expenny.service.domain.controller.tag.TagController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.tags(tagController: TagController) {
    authenticate {
        authorize(UserRole.SUPER_USER) {
            route("/tags") {
                get { tagController.getTags(this.context) }
                post { tagController.createTag(this.context) }
                delete { tagController.deleteTags(this.context) }

                route("/{tagId}") {
                    get { tagController.getTag(this.context) }
                    put { tagController.updateTag(this.context) }
                    delete { tagController.deleteTag(this.context) }
                }
            }
        }
    }
}