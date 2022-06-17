package io.ducket.api.routes

import io.ducket.api.auth.UserRole
import io.ducket.api.auth.authorization.authorize
import io.ducket.api.domain.controller.group.GroupController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.groups(groupController: GroupController) {
    authenticate {
        authorize(UserRole.SUPER_USER) {
            route("/groups") {
                get { groupController.getGroups(this.context) }
                post { groupController.addGroup(this.context) }

                route("/{groupId}") {
                    get { groupController.getGroup(this.context) }
                    put { groupController.updateGroup(this.context) }
                    delete { groupController.deleteGroup(this.context) }

                    route("/members") {
                        post { groupController.addGroupMember(this.context) }
                        // get { groupController.getGroupMembers(this.context) }

                        route("/{membershipId}") {
                            // get { userController.getGroupMember(this.context) }
                            delete { groupController.deleteGroupMember(this.context) }
                            put { groupController.updateGroupMember(this.context) }
                        }
                    }
                }
            }
        }
    }
}