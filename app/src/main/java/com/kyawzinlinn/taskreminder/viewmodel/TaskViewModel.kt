package com.kyawzinlinn.taskreminder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.repository.TaskRepository
import kotlinx.coroutines.launch

enum class OperationStatus { LOADING, DONE, ERROR }

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _status = MutableLiveData<OperationStatus>()
    val status = _status

    val taskList = repository.allTasks.asLiveData()

    fun addTask(task: Task) {
        try {
            _status.value = OperationStatus.LOADING
            viewModelScope.launch {
                repository.insertTask(task)
                _status.value = OperationStatus.DONE
            }
        } catch (e: Exception) {
            _status.value = OperationStatus.ERROR
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch { repository.deleteTask(task) }

    fun getTask(title: String, description: String) = repository.getTask(title, description)

    fun updateToDo(task: Task) = try {
        _status.value = OperationStatus.LOADING
        viewModelScope.launch {
            repository.updateTask(task)
            _status.value = OperationStatus.DONE
        }
    } catch (e: Exception) {
        _status.value = OperationStatus.ERROR
    }
}

class ToDoViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}