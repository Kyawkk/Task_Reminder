package com.kyawzinlinn.taskreminder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.repository.TaskRepository
import com.kyawzinlinn.taskreminder.util.convertDateAndTimeToSeconds
import com.kyawzinlinn.taskreminder.worker.TaskReminderWorker
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class ToDoViewModel(private val repository: TaskRepository): ViewModel() {

    private val workManager = WorkManager.getInstance()

    val taskList = repository.allTasks.asLiveData()

    suspend fun searchToDoList(query: String) = viewModelScope.launch { repository.searchTask(query) }

    fun addTask(task: Task){
        viewModelScope.launch {
            repository.insertTask(task)
            scheduleReminder(task)
        }
    }

    private fun scheduleReminder(task: Task){
        val data = Data.Builder()
        data.putString(TaskReminderWorker.taskTitleKey, task.title)
        data.putString(TaskReminderWorker.taskDescriptionKey, task.description)
        data.putString(TaskReminderWorker.taskId, task.id.toString())

        val duration = convertDateAndTimeToSeconds(task.date,task.time)

        Log.d("TAG", "scheduleReminder: $duration")

        val reminderWorker = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(3,TimeUnit.SECONDS)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniqueWork(
            task.title,
            ExistingWorkPolicy.REPLACE,
            reminderWorker
        )
    }

    fun deleteTask(task: Task) = viewModelScope.launch { repository.deleteTask(task) }

    fun updateToDo(task: Task) = viewModelScope.launch { repository.updateTask(task) }
}

class ToDoViewModelFactory(private val repository: TaskRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}