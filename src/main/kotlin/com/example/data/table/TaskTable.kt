package com.example.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.booleanLiteral
import org.jetbrains.exposed.sql.booleanParam

object TaskTable: Table() {
    val id = varchar("id",512)
    val userEmail = varchar("userEmail",512).references(UserTable.email)
    val note = text("note")
    val title = text("title")
    val createdAt = long("createdAt")
    val isCompleted = bool("isCompleted")
    val isImportant = bool("isImportant")
    val listTaskId = varchar("listTaskId",512)
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}