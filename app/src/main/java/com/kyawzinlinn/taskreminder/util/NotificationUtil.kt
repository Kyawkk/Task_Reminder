package com.kyawzinlinn.taskreminder.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kyawzinlinn.taskreminder.R
import com.kyawzinlinn.taskreminder.receiver.NotificationReceiver


object NotificationUtil {
    private const val CHANNEL_ID = "com.kyawzinlinn.taskreminder.channel_id"
    const val NOTIFICATION_ID = 1

    fun makeStatusNotification(id: String, title: String, message: String, context: Context) {
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, title, importance)
            channel.setSound(soundUri, audioAttributes)
            channel.description = message
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC

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
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(LongArray(1))
            .setSound(soundUri)

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = UPDATE_DATABASE_ACTION
        }

        intent.putExtra(UPDATE_TASK_ID, id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_background,
            "Complete",
            pendingIntent
        ).build()

        builder.addAction(action)

        // Show the notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun requestNotificationPermission(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)
}