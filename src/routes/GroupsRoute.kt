package io.ducket.api.routes

import io.ducket.api.domain.controller.group.GroupController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.groups(
    groupController: GroupController,
) {
    authenticate {
        route("/groups") {
            post { groupController.createGroup(this.context) }
            get { groupController.getGroups(this.context) }
            delete { groupController.deleteGroups(this.context) }

            route("/{groupId}") {
                get { groupController.getGroup(this.context) }
                put { groupController.updateGroup(this.context) }
                delete { groupController.deleteGroup(this.context) }

                route("/memberships") {
                    post { groupController.addGroupMembership(this.context) }
                    patch { groupController.deleteGroupMemberships(this.context) }

                    route("/{membershipId}") {
                        post { groupController.updateGroupMembership(this.context) }
                    }
                }
            }
        }
    }
}