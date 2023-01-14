package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.repository.*
import io.ktor.server.application.*
import com.example.routes.GroupListRoutes
import com.example.routes.ListTaskRoutes
import com.example.routes.TaskRoutes
import com.example.routes.UserRoutes
import io.ktor.serialization.gson.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.locations.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(KtorExperimentalLocationsAPI::class)
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()

    val userRepository = UserRepository()
    val taskRepository = TaskRepository()
    val listTaskRepository = ListTaskRepository()
    val groupListRepository = GroupListRepository()

    val jwtService= JwtService()
    val hashFunction = {s:String->hash(s)}

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate{
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = userRepository.findUserByEmail(email)
                user
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(Locations)
    routing {
        UserRoutes(userRepository,jwtService,hashFunction)
        TaskRoutes(taskRepository)
        GroupListRoutes(groupListRepository)
        ListTaskRoutes(listTaskRepository)
    }
}

data class MySession(val count: Int = 0)
