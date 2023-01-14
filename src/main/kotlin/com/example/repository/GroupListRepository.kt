package com.example.repository

import com.example.data.model.GroupList
import com.example.data.table.GroupListTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class GroupListRepository {

    suspend fun addGroupList(groupList: GroupList, email: String) {
        DatabaseFactory.dbQuery {
            GroupListTable.insert { nt ->
                nt[id] = groupList.id
                nt[name] = groupList.name
                nt[createdAt] = groupList.createdAt
                nt[userEmail] = email
            }
        }
    }


    suspend fun getGroupsListByEmail(email: String): List<GroupList> = DatabaseFactory.dbQuery {
        GroupListTable.select {
            GroupListTable.userEmail.eq(email)
        }.mapNotNull { rowToGroupList(it) }
    }


    suspend fun updateGroupList(groupList: GroupList, email: String) {

        DatabaseFactory.dbQuery {
            GroupListTable.update(
                where = {
                    GroupListTable.id.eq(groupList.id) and GroupListTable.userEmail.eq(
                        email
                    )
                }
            ) { nt ->
                nt[name] = groupList.name
                nt[createdAt] = groupList.createdAt
            }
        }
    }

    suspend fun deleteGroupList(id: String, email: String) {
        DatabaseFactory.dbQuery {
            GroupListTable.deleteWhere { GroupListTable.id.eq(id) and GroupListTable.userEmail.eq(email) }
        }
    }


    private fun rowToGroupList(row: ResultRow?): GroupList? {
        if (row == null) {
            return null
        }

        return GroupList(
            id = row[GroupListTable.id],
            name = row[GroupListTable.name],
            createdAt = row[GroupListTable.createdAt],
        )
    }

}