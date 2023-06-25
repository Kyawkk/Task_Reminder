package com.kyawzinlinn.taskreminder.repository

import androidx.lifecycle.asLiveData
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.database.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getTaskList()

    suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    fun getTask(title: String, description: String) =
        taskDao.getTask(title, description).asLiveData()
}