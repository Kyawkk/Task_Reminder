package com.kyawzinlinn.taskreminder

import android.app.Application
import com.kyawzinlinn.taskreminder.database.TaskDatabase

class App: Application() {
    val database: TaskDatabase by lazy { TaskDatabase.getDatabase(this) }
}