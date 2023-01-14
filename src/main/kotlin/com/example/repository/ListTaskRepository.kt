package com.example.repository

import com.example.data.model.ListTask
import com.example.data.table.ListTaskTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ListTaskRepository {

    suspend fun addListTask(listTask: ListTask, email: String) {
        dbQuery {
            ListTaskTable.insert { nt ->
                nt[ListTaskTable.id] = listTask.id
                nt[ListTaskTable.name] = listTask.name
                nt[ListTaskTable.createdAt] = listTask.createdAt
                nt[ListTaskTable.color] = listTask.color
                nt[ListTaskTable.groupListId] = listTask.groupId
                nt[ListTaskTable.userEmail] = email
            }
        }
    }


    suspend fun getListTasksByGroupId(email: String, groupId: String): List<ListTask> = dbQuery {
        ListTaskTable.select {
            ListTaskTable.userEmail.eq(email) and ListTaskTable.groupListId.eq(groupId)
        }.mapNotNull { rowToListTask(it) }

    }
    suspend fun getListTasks(email: String): List<ListTask> = dbQuery {
        ListTaskTable.select {
            ListTaskTable.userEmail.eq(email)
        }.mapNotNull { rowToListTask(it) }

    }


    suspend fun updateListTask(listTask: ListTask, email: String) {

        dbQuery {
            ListTaskTable.update(
                where = {
                    ListTaskTable.id.eq(listTask.id) and ListTaskTable.userEmail.eq(email)
                }
            ) { nt ->
                nt[ListTaskTable.name] = listTask.name
                nt[ListTaskTable.color] = listTask.color
                nt[ListTaskTable.createdAt] = listTask.createdAt
                nt[ListTaskTable.groupListId] = listTask.groupId
            }
        }
    }

    suspend fun deleteListTask(id: String, email: String) {
        dbQuery {
            ListTaskTable.deleteWhere { ListTaskTable.userEmail.eq(email) and ListTaskTable.id.eq(id) }
        }
    }


    private fun rowToListTask(row: ResultRow?): ListTask? {
        if (row == null) {
            return null
        }

        return ListTask(
            id = row[ListTaskTable.id],
            name = row[ListTaskTable.name],
            color = row[ListTaskTable.color],
            createdAt = row[ListTaskTable.createdAt],
            groupId = row[ListTaskTable.groupListId]
        )
    }
}