package com.kyawzinlinn.taskreminder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kyawzinlinn.taskreminder.data.models.TaskWithDate
import com.kyawzinlinn.taskreminder.util.convertDateAndTimeToSeconds
import java.io.Serializable

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

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

    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean,
): Serializable

fun List<Task>.format(): List<TaskWithDate>{
    return groupBy(
        {
            if (it.isCompleted) "Completed"
            else if (!it.isCompleted && convertDateAndTimeToSeconds(it.date,it.time) <= 0) "Overdue"
            else it.date
        },
        {it})
        .map { mapData ->
        TaskWithDate(mapData.key,mapData.value)
    }.sortedBy { it.title }
}