package com.kyawzinlinn.taskreminder

import android.app.Application
import com.kyawzinlinn.taskreminder.database.TaskDatabase
import com.kyawzinlinn.taskreminder.repository.TaskRepository

class App : Application() {
    val database: TaskDatabase by lazy { TaskDatabase.getDatabase(this) }
    val repository: TaskRepository by lazy { TaskRepository(database.taskDao()) }
}