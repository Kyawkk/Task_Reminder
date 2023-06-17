package com.kyawzinlinn.taskreminder.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao{
    @Query("select * from task ORDER BY title")
    fun getTaskList(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("update task set isFinished = 'true' where id = :id")
    fun completeTask(id: String)

    @Query("select * from task where title = :query")
    fun searchTask(query: String): Flow<List<Task>>

}