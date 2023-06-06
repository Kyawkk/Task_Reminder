package com.kyawzinlinn.todoreminder.data.models

data class ToDoWithDay(
    val title: String,
    val todoList: List<ToDo>,
    var isExpanded: Boolean = true
)
