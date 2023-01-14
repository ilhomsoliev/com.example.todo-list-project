package com.example.repository

import com.example.data.model.Task
import com.example.data.table.TaskTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TaskRepository {
    suspend fun addTask(task: Task, email: String){
        DatabaseFactory.dbQuery {
            TaskTable.insert { nt ->
                nt[TaskTable.id] = task.id
                nt[TaskTable.userEmail] = email
                nt[TaskTable.note] = task.note
                nt[TaskTable.title] = task.title
                nt[TaskTable.isCompleted] = task.isCompleted
                nt[TaskTable.isImportant] = task.isImportant
                nt[TaskTable.createdAt] = task.createdAt
                nt[TaskTable.listTaskId] = task.listTaskId
            }
        }
    }


    suspend fun getTasks(email:String):List<Task> = DatabaseFactory.dbQuery {
        TaskTable.select {
            TaskTable.userEmail.eq(email)
        }.mapNotNull { rowToTask(it) }
    }

    suspend fun getTasksByListId(email:String,listId:String):List<Task> = DatabaseFactory.dbQuery {
        TaskTable.select {
            TaskTable.userEmail.eq(email) and TaskTable.listTaskId.eq(listId)
        }.mapNotNull { rowToTask(it) }
    }


    suspend fun updateTask(task: Task, email: String){

        DatabaseFactory.dbQuery {
            TaskTable.update(
                where = {
                    TaskTable.userEmail.eq(email) and TaskTable.id.eq(task.id)
                }
            ) { nt ->
                nt[note] = task.note
                nt[isCompleted] = task.isCompleted
                nt[isImportant] = task.isImportant
                nt[createdAt] = task.createdAt
                nt[listTaskId] = task.listTaskId
            }
        }
    }

    suspend fun deleteTask(id:String, email: String){
        DatabaseFactory.dbQuery {
            TaskTable.deleteWhere { userEmail.eq(email) and TaskTable.id.eq(id) }
        }
    }


    private fun rowToTask(row: ResultRow?): Task? {
        if (row == null) {
            return null
        }

        return Task(
            id = row[TaskTable.id],
            note = row[TaskTable.note],
            isCompleted = row[TaskTable.isCompleted],
            isImportant = row[TaskTable.isImportant],
            createdAt = row[TaskTable.createdAt],
            title = row[TaskTable.title],
            listTaskId = row[TaskTable.listTaskId],
        )
    }
}