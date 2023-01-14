package com.example.routes

import com.example.data.model.ListTask
import com.example.data.model.Task
import com.example.data.model.User
import com.example.data.model.dto.SimpleResponse
import com.example.repository.ListTaskRepository
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

const val LISTTASKS = "$API_VERSION/listtasks"
const val LISTTASKS_GET_BY_GROUP_ID = "$API_VERSION/listtasks_by_group_id"
const val CREATE_LISTTASKS = "$LISTTASKS/create"
const val UPDATE_LISTTASKS = "$LISTTASKS/update"
const val DELETE_LISTTASKS = "$LISTTASKS/delete"

@Location(CREATE_LISTTASKS)
class ListTaskCreateRoute

@Location(LISTTASKS)
class ListsTaskGet()

@Location(LISTTASKS_GET_BY_GROUP_ID)
class ListsTaskGetByGroupIdRoute()

@Location(UPDATE_LISTTASKS)
class ListTaskUpdateRoute

@Location(DELETE_LISTTASKS)
class ListTaskDeleteRoute

fun Route.ListTaskRoutes(
    db: ListTaskRepository,
) {
    authenticate("jwt") {

        post<ListTaskCreateRoute> {
            val listTask = try {
                call.receive<ListTask>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
               /* if (listTask.id.isNotEmpty()) {
                    db.updateListTask(listTask, email)
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, listTask.id))
                    return@post
                }
                listTask.id = UUID.randomUUID().toString()*/
                db.addListTask(listTask, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, listTask.id))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }

        }

        get<ListsTaskGetByGroupIdRoute> {
            try {
                val groupId = call.request.queryParameters["groupId"]!!.toString()
                val email = call.principal<User>()!!.email
                val listTasks = db.getListTasksByGroupId(email = email, groupId = groupId)
                call.respond(HttpStatusCode.OK, listTasks)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }

        get<ListsTaskGet> {
            try {
                val email = call.principal<User>()!!.email
                val listTasks = db.getListTasks(email = email)
                call.respond(HttpStatusCode.OK, listTasks)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }

        post<ListTaskUpdateRoute> {
            val listTask = try {
                call.receive<ListTask>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                db.updateListTask(listTask, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "ListTask Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }

        }


        delete<ListTaskDeleteRoute> {

            val listTaskId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter:id is not present"))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteListTask(listTaskId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "ListTask Deleted Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred!"))
            }

        }
    }
}