package com.example.routes

import com.example.data.model.GroupList
import com.example.data.model.Task
import com.example.data.model.User
import com.example.data.model.dto.SimpleResponse
import com.example.repository.GroupListRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Exception
import java.util.*

const val GROUPLIST = "$API_VERSION/grouplist"
const val CREATE_GROUPLIST = "$GROUPLIST/create"
const val UPDATE_GROUPLIST = "$GROUPLIST/update"
const val DELETE_GROUPLIST = "$GROUPLIST/delete"

@Location(CREATE_GROUPLIST)
class GroupListCreateRoute

@Location(GROUPLIST)
class GroupListRoutes

@Location(UPDATE_GROUPLIST)
class GroupListUpdateRoute

@Location(DELETE_GROUPLIST)
class GroupListDeleteRoute

@OptIn(KtorExperimentalLocationsAPI::class)
fun Route.GroupListRoutes(
    db: GroupListRepository,
) {
    authenticate("jwt") {

        post<GroupListCreateRoute> {
            val groupList = try {
                call.receive<GroupList>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                //  groupList.id = UUID.randomUUID().toString()*/
                db.addGroupList(groupList, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "GroupList Added Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }

        }


        get<GroupListRoutes> {
            try {
                val email = call.principal<User>()!!.email
                val groupList: List<GroupList> = db.getGroupsListByEmail(email = email)
                call.respond(HttpStatusCode.OK, groupList)
            } catch (e: Exception) {
                println("${e.message} HERE")
                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }



        post<GroupListUpdateRoute> {

            val groupList = try {
                call.receive<GroupList>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                db.updateGroupList(groupList, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "GroupList Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }

        }


        delete<GroupListDeleteRoute> {

            val listTaskId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter:id is not present"))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteGroupList(listTaskId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "ListTask Deleted Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred!"))
            }
        }
    }
}