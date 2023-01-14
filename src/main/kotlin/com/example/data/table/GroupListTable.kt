package com.example.data.table

import com.example.data.table.TaskTable.references
import org.jetbrains.exposed.sql.Table

object GroupListTable: Table() {
    val id = varchar("id",512)
    val name = text("name")
    val createdAt = long("createdAt")
    val userEmail = varchar("userEmail",512).references(UserTable.email)
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}