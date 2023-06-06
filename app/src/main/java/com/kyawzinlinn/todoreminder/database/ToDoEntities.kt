package com.kyawzinlinn.todoreminder.database

import android.icu.util.LocaleData
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kyawzinlinn.todoreminder.data.models.ToDo
import com.kyawzinlinn.todoreminder.data.models.ToDoWithDay
import java.time.LocalDate
import java.util.Locale

@Entity(tableName = "todo")
data class ToDoEntities(
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

    @ColumnInfo(name = "isFinished")
    val isFinished: Boolean,
)

fun List<ToDoEntities>.asToDoModel(): List<ToDo>{
    return map{
        ToDo(
            it.id,
            it.title,
            it.description,
            it.date,
            it.time,
            it.isFinished
        )
    }
}

val today = LocalDate.now().toString()

fun List<ToDoEntities>.format(): List<ToDoWithDay>{
    return groupBy(
        {
            Log.d("TAG", "format: ${it.isFinished}")
            if (it.isFinished) "Finished"
            else if (isToday(it.date) && !it.isFinished) "Today"
            else if (isTomorrow(it.date) && !it.isFinished) "Tomorrow"
            else it.date},
        {it})
        .map { mapData ->
        ToDoWithDay(mapData.key,mapData.value.asToDoModel())
    }.sortedBy { it.title }
}

fun isTomorrow(date: String): Boolean {
    val tomorrow = LocalDate.now().plusDays(1).toString()

    Log.d("TAG", "isTomorrow: ${date.equals(tomorrow)}")

    return date.equals(tomorrow)
}

private fun isToday(date: String): Boolean{
    return today.equals(date)
}