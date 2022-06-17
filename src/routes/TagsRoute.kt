package io.ducket.api.routes

import io.ducket.api.auth.UserRole
import io.ducket.api.auth.authorization.authorize
import io.ducket.api.domain.controller.tag.TagController
import io.ktor.auth.*
import io.ktor.routing.*

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