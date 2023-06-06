package com.kyawzinlinn.todoreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kyawzinlinn.todoreminder.data.models.ToDo
import com.kyawzinlinn.todoreminder.database.ToDoDao
import com.kyawzinlinn.todoreminder.database.ToDoEntities
import com.kyawzinlinn.todoreminder.database.asToDoEntities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ToDoViewModel(private val toDoDao: ToDoDao): ViewModel() {

    val toDoList = toDoDao.getTodDoList().asLiveData()

    suspend fun searchToDoList(query: String) = toDoDao.searchTodo(query).asLiveData()

    fun addToDo(todo: ToDoEntities){
        viewModelScope.launch {
            toDoDao.insert(todo)
        }
    }

    fun updateToDo(toDo: ToDo) = viewModelScope.launch { toDoDao.update(toDo.asToDoEntities()) }
}

class ToDoViewModelFactory(private val toDoDao: ToDoDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(toDoDao) as T
        }
        throw IllegalArgumentException("Unknow ViewModel Class")
    }
}