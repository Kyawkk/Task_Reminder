package com.kyawzinlinn.todoreminder.data.models

data class ToDo(
    val id: Int,
    val title: String = "",
    val description: String = "",
    val date: String,
    val time: String = "",
    val isFinished: Boolean = false
)
