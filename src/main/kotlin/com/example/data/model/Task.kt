package com.example.data.model

data class Task(
    var id:String = "",
    var createdAt:Long,
    var isCompleted:Boolean,
    var isImportant:Boolean,
    var title:String,
    var note:String,
    var listTaskId:String,
)