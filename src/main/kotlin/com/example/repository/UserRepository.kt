package com.example.repository

import com.example.data.model.User
import com.example.data.table.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserRepository {
    suspend fun addUser(user: User){
        DatabaseFactory.dbQuery {
            UserTable.insert { ut ->
                ut[email] = user.email
                ut[hashPassword] = user.hashPassword
                ut[name] = user.userName
            }
        }
    }
    suspend fun findUserByEmail(email:String) = DatabaseFactory.dbQuery {
        UserTable.select {
            UserTable.email.eq(email)
        }.map { rowToUser(it) }.singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User?{
        if(row == null) return null
        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }

}