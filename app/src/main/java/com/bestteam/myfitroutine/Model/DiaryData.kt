package com.bestteam.myfitroutine.Model

data class DiaryData(
    val date: String,
    val todos: List<Todo>
)

data class Todo(
    val context: String = "할일을 작성하세요",
    val date:String
)
