package com.kyawzinlinn.taskreminder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.util.TASK_INTENT_EXTRA
import com.kyawzinlinn.taskreminder.util.WorkerUtils.scheduleReminder

class TaskReminderService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val task = intent?.extras?.getSerializable(TASK_INTENT_EXTRA) as Task
        scheduleReminder(task)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}