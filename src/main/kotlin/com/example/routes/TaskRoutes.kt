package com.example.routes

import com.example.data.model.*
import com.example.data.model.dto.SimpleResponse
import com.example.repository.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Exception
import java.util.UUID


const val TASKS = "$API_VERSION/tasks"
const val TASKS_BY_LIST_ID = "$API_VERSION/tasks-by-list-id"
const val CREATE_TASKS = "$TASKS/create"
const val UPDATE_TASKS = "$TASKS/update"
const val DELETE_TASKS = "$TASKS/delete"

@Location(CREATE_TASKS)
class TaskCreateRoute

@Location(TASKS)
class TaskGetRoute
@Location(TASKS_BY_LIST_ID)
class TaskByListIdGetRoute

@Location(UPDATE_TASKS)
class TaskUpdateRoute

@Location(DELETE_TASKS)
class TaskDeleteRoute

fun Route.TaskRoutes(
    db: TaskRepository,
){
    authenticate("jwt") {
        post<TaskCreateRoute>{
            val task = try {
                call.receive<Task>()
            } catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Missing Fields"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email

                db.addTask(task,email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true,"Task Added Successfully!"))
            } catch (e: Exception){
                println("HELLO ${e.message}")
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false,e.message ?: "Some Problem Occurred!"))
            }

        }


        get<TaskGetRoute>{

            try {
                val email = call.principal<User>()!!.email
                val tasks: List<Task> = db.getTasks(email = email)
                call.respond(HttpStatusCode.OK,tasks)
            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }

        get<TaskByListIdGetRoute>{
            try {
                val listId = call.request.queryParameters["listId"]!!.toString()
                val email = call.principal<User>()!!.email
                val tasks: List<Task> = db.getTasksByListId(email = email,listId = listId)
                call.respond(HttpStatusCode.OK,tasks)
            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }



        post<TaskUpdateRoute> {

            val task = try {
                call.receive<Task>()
            } catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Missing Fields"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                db.updateTask(task,email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true,"Note Updated Successfully!"))
            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false,e.message ?: "Some Problem Occurred!"))
            }

        }


        delete<TaskDeleteRoute> {

            val noteId = try{
                call.request.queryParameters["id"]!!
            }catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"QueryParameter:id is not present"))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteTask(noteId,email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true,"Note Deleted Successfully!"))
            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred!"))
            }

        }
    }
}