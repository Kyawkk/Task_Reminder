package com.kyawzinlinn.taskreminder.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentManager
import androidx.room.util.wrapMappedColumns
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.kyawzinlinn.taskreminder.R
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.DeleteTaskDialogBinding
import com.kyawzinlinn.taskreminder.receiver.NotificationReceiver
import com.kyawzinlinn.taskreminder.ui.MainActivity
import com.kyawzinlinn.taskreminder.worker.TaskReminderWorker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

fun showDatePicker(fragmentManager: FragmentManager, onDatePicked: (String) ->  Unit){
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select Date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    datePicker.addOnPositiveButtonClickListener {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        onDatePicked(formatter.format(it))
    }

    datePicker.show(fragmentManager,"datePicker")
}

fun showTimePicker(fragmentManager: FragmentManager, onTimePicked: (String) -> Unit){
    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setTitleText("Select Time")
        .build()


    picker.addOnPositiveButtonClickListener {
        onTimePicked(formatHour(picker.hour,picker.minute))
    }

    picker.show(fragmentManager,"timePicker")
}

fun formatHour(hour: Int, minute: Int): String{

    val formatter = SimpleDateFormat("H:mm:ss")
    val dateObj = formatter.parse("$hour:$minute:00")

    return SimpleDateFormat("hh:mm:ss a", Locale.US).format(dateObj)
}

fun isTomorrow(date: String): Boolean {
    val tomorrow = LocalDate.now().plusDays(1).toString()
    return date.equals(tomorrow)
}

private fun isToday(date: String): Boolean{
    val today = LocalDate.now().toString()
    return today.equals(date)
}

fun showDeleteTaskDialog(context: Context, onDelete: () -> Unit){
    val builder = MaterialAlertDialogBuilder(context)
    val dialogBinding = DeleteTaskDialogBinding.inflate(LayoutInflater.from(context))

    builder.background = ColorDrawable(Color.TRANSPARENT)
    val dialog = builder.setView(dialogBinding.root).create()
    dialogBinding.btnDelete.setOnClickListener {
        onDelete()
        dialog.dismiss()
    }

    dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
    dialog.show()
}

fun convertDateAndTimeToSeconds(dateString: String, timeString: String): Long{
    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
    val date = format.parse("$dateString $timeString")

    val calendar = Calendar.getInstance()
    calendar.time = date

    val milliseconds = calendar.timeInMillis

    val todayTime = Calendar.getInstance()

    return (milliseconds / 1000L) - (todayTime.timeInMillis / 1000L)
}