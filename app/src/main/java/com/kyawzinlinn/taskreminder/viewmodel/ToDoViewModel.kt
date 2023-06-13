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
import com.kyawzinlinn.taskreminder.database.TaskDao
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.util.convertDateAndTimeToSeconds
import com.kyawzinlinn.taskreminder.worker.TaskReminderWorker
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class ToDoViewModel(private val toDoDao: TaskDao): ViewModel() {

    private val workManager = WorkManager.getInstance()

    val taskList = toDoDao.getTaskList().asLiveData()

    suspend fun searchToDoList(query: String) = toDoDao.searchTask(query).asLiveData()

    fun addTask(task: Task){
        viewModelScope.launch {
            toDoDao.insert(task)
            scheduleReminder(task)
        }
    }

    private fun scheduleReminder(task: Task){
        val data = Data.Builder()
        data.putString(TaskReminderWorker.taskTitleKey, task.title)
        data.putString(TaskReminderWorker.taskDescriptionKey, task.description)

        val duration = convertDateAndTimeToSeconds(task.date,task.time)

        Log.d("TAG", "scheduleReminder: $duration")

        val reminderWorker = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(duration,TimeUnit.SECONDS)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniqueWork(
            task.title,
            ExistingWorkPolicy.REPLACE,
            reminderWorker
        )
    }

    fun deleteTask(toDo: Task) = viewModelScope.launch { toDoDao.delete(toDo) }

    fun updateToDo(toDo: Task) = viewModelScope.launch { toDoDao.update(toDo) }
}

class ToDoViewModelFactory(private val taskDao: TaskDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}