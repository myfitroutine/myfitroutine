package com.bestteam.myfitroutine.Model

data class DiaryData(
    val date: String,
    val todos: List<Todo>
)

data class Todo(
    val content: String,
    val check:Boolean = false
)
