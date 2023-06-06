package com.kyawzinlinn.todoreminder.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kyawzinlinn.todoreminder.data.models.ToDo
import com.kyawzinlinn.todoreminder.data.models.ToDoWithDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Dao
interface ToDoDao{
    @Query("select * from todo ORDER BY title")
    fun getTodDoList(): Flow<List<ToDoEntities>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: ToDoEntities)

    @Update
    suspend fun update(todo: ToDoEntities)

    @Query("select * from todo where title = :query")
    fun searchTodo(query: String): Flow<List<ToDoEntities>>

    fun ToDoEntities.asToDo(): ToDo{
        return ToDo(
            id, title, description, date, time, isFinished
        )
    }

}

fun ToDo.asToDoEntities(): ToDoEntities{
    return ToDoEntities(
        id, title, description, date, time, isFinished
    )
}