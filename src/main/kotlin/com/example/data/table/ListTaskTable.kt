package com.example.data.table

import org.jetbrains.exposed.sql.Table

object ListTaskTable: Table() {
    val id = varchar("id",512)
    val name = text("name")
    val createdAt = long("createdAt")
    val color = integer("color")
    val groupListId = varchar("groupListId", 512)
    val userEmail = varchar("userEmail",512).references(UserTable.email)
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}