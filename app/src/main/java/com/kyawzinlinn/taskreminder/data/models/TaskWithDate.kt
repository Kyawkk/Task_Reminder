package com.kyawzinlinn.taskreminder.data.models

import com.kyawzinlinn.taskreminder.database.Task

data class TaskWithDate(
    val title: String,
    val todoList: List<Task>,
    var isExpanded: Boolean = true
)
