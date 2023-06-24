package com.kyawzinlinn.taskreminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.database.TaskDatabase
import com.kyawzinlinn.taskreminder.util.UPDATE_DATABASE_ACTION
import com.kyawzinlinn.taskreminder.util.UPDATE_TASK_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(ctx: Context?, intent: Intent?) {
        val action = intent!!.action
        val taskId = intent.getStringExtra(UPDATE_TASK_ID)

        if (action == UPDATE_DATABASE_ACTION){
            val database = TaskDatabase.getDatabase(ctx!!)
            val dao = database.taskDao()
            CoroutineScope(Dispatchers.IO).launch {
                dao.completeTask(taskId.toString())
                Log.d("TAG", "onReceive: complete $taskId")
            }
        }
    }
}