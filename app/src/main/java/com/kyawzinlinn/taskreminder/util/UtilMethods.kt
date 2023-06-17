package com.kyawzinlinn.taskreminder.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentManager
import androidx.room.util.wrapMappedColumns
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.kyawzinlinn.taskreminder.R
import com.kyawzinlinn.taskreminder.databinding.DeleteTaskDialogBinding
import com.kyawzinlinn.taskreminder.receiver.NotificationReceiver
import com.kyawzinlinn.taskreminder.ui.MainActivity
import com.kyawzinlinn.taskreminder.worker.TaskReminderWorker.Companion.taskId
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

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

    val formatter = SimpleDateFormat("H:mm")
    val dateObj = formatter.parse("$hour:$minute")

    return SimpleDateFormat("hh:mm a", Locale.US).format(dateObj)
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

    builder.setBackground(ColorDrawable(Color.TRANSPARENT))
    val dialog = builder.setView(dialogBinding.root).create()
    dialogBinding.btnDelete.setOnClickListener {
        onDelete()
        dialog.dismiss()
    }

    dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
    dialog.show()
}

fun makeStatusNotification(id: String, title: String, message: String, context: Context) {

    val CHANNEL_ID = "2432"

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, title, importance)
        channel.description = message

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        action = UPDATE_DATABASE_ACTION
    }
    intent.putExtra(UPDATE_TASK_ID, id)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action = NotificationCompat.Action.Builder(
        R.drawable.ic_launcher_background,
        "Done",
        pendingIntent
    ).build()

    builder.addAction(action)

    // Show the notification
    NotificationManagerCompat.from(context).notify(1, builder.build())
}

fun convertDateAndTimeToSeconds(dateString: String, timeString: String): Long{
    val date = SimpleDateFormat("yyyy-MM-dd").parse(dateString)
    val time = SimpleDateFormat("hh:mm a").parse(timeString)

    val calendar = Calendar.getInstance()

    val todayTime = Calendar.getInstance()

    calendar.set(calendar.get(Calendar.YEAR),date.month,date.day,time.hours, time.minutes)
    Log.d("TAG", "convertDateAndTimeToSeconds: ${calendar.timeInMillis} || ${todayTime.timeInMillis}")

    return (calendar.timeInMillis / 1000L) - (todayTime.timeInMillis / 1000L)
}