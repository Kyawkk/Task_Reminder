package com.kyawzinlinn.taskreminder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kyawzinlinn.taskreminder.data.models.TaskWithDate
import com.kyawzinlinn.taskreminder.util.isTomorrow

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "delayTime")
    val delayTime: Int,

    @ColumnInfo(name = "isFinished")
    val isFinished: Boolean,
)

fun List<Task>.format(): List<TaskWithDate>{
    return groupBy(
        {
            if (it.isFinished) "Finished"
            else it.date
        },
        {it})
        .map { mapData ->
        TaskWithDate(mapData.key,mapData.value)
    }.sortedBy { it.title }
}