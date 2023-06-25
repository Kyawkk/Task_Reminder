package com.kyawzinlinn.taskreminder.util

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.worker.TaskReminderWorker
import java.util.concurrent.TimeUnit

object WorkerUtils {
    fun scheduleReminder(task: Task?) {

        val workManager = WorkManager.getInstance()

        val data = Data.Builder()

        data.putString(TaskReminderWorker.taskTitleKey, task?.title)
        data.putString(TaskReminderWorker.taskDescriptionKey, task?.description)
        data.putString(TaskReminderWorker.taskIdKey, task?.id.toString())

        val duration = convertDateAndTimeToSeconds(task!!.date, task.time)

        val reminderWorker = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(duration, TimeUnit.SECONDS)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniqueWork(
            task.title,
            ExistingWorkPolicy.REPLACE,
            reminderWorker
        )
    }
}
