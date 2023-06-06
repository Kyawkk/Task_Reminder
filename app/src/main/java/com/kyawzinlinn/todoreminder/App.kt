package com.kyawzinlinn.todoreminder

import android.app.Application
import com.kyawzinlinn.todoreminder.database.ToDoDatabase

class App: Application() {
    val database: ToDoDatabase by lazy { ToDoDatabase.getDatabase(this) }
}