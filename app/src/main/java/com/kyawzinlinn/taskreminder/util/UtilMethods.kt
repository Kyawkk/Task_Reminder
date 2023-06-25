package com.kyawzinlinn.taskreminder.util

import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

fun showDatePicker(fragmentManager: FragmentManager, onDatePicked: (String) -> Unit) {
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select Date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    datePicker.addOnPositiveButtonClickListener {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        onDatePicked(formatter.format(it))
    }

    datePicker.show(fragmentManager, "datePicker")
}

fun showTimePicker(fragmentManager: FragmentManager, onTimePicked: (String) -> Unit) {
    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setTitleText("Select Time")
        .build()


    picker.addOnPositiveButtonClickListener {
        onTimePicked(formatHour(picker.hour, picker.minute))
    }
    picker.show(fragmentManager, "timePicker")
}

fun formatHour(hour: Int, minute: Int): String {

    val formatter = SimpleDateFormat("H:mm:ss")
    val dateObj = formatter.parse("$hour:$minute:00")

    return SimpleDateFormat("hh:mm:ss a", Locale.US).format(dateObj)
}

fun isTomorrow(date: String): Boolean {
    val tomorrow = LocalDate.now().plusDays(1).toString()
    return date.equals(tomorrow)
}

fun isToday(date: String): Boolean {
    val today = LocalDate.now().toString()
    return today.equals(date)
}

fun convertDateAndTimeToSeconds(dateString: String, timeString: String): Long {
    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
    val date = format.parse("$dateString $timeString")

    val calendar = Calendar.getInstance()
    calendar.time = date

    val milliseconds = calendar.timeInMillis

    val todayTime = Calendar.getInstance()

    return (milliseconds / 1000L) - (todayTime.timeInMillis / 1000L)
}

fun showCheckNotificationPermissionDialog(context: Context) {
    val builder = MaterialAlertDialogBuilder(context)
    builder.setTitle("Important")
    builder.setMessage("You have to enable notification permission in order to receive notification.")
    builder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
            requestNotificationPermission(context)
        }
    })
    builder.setCancelable(false)
    builder.create().show()
}