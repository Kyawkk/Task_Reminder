package com.kyawzinlinn.taskreminder.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kyawzinlinn.taskreminder.util.makeStatusNotification

class TaskReminderWorker(private val context: Context, params: WorkerParameters): Worker(context,params) {

    companion object {
        const val taskTitleKey = "TASK_TITLE"
        const val taskDescriptionKey = "TASK_DESCRIPTION"
        const val taskId = "TASK_ID"
    }

    override fun doWork(): Result {
        val taskTitle = inputData.getString(taskTitleKey)
        val taskDesc = inputData.getString(taskDescriptionKey)
        val taskId = inputData.getString(taskId)

        makeStatusNotification(taskId!!, taskTitle!!, taskDesc!!,context)

        return Result.success()
    }
}