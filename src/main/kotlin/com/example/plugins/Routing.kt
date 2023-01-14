package com.example.plugins

import com.example.authentication.JwtService
import com.example.repository.ListTaskRepository
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting(db: ListTaskRepository, jwtService: JwtService, hashFunction: (String) -> String) {

    routing {
        get("/"){
            call.respondText("Hello World")
        }

    }
}
