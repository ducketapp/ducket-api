package dev.ducketapp.service.routes

import dev.ducketapp.service.auth.authentication.UserRole
import dev.ducketapp.service.auth.authorization.authorize
import dev.ducketapp.service.domain.controller.tag.TagController
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